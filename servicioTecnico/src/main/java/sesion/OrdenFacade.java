package sesion;

import entidad.Item;
import entidad.Orden;
import entidad.OrdenItem;
import entidad.OrdenItemPK;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 * Lógica de negocio de las órdenes de reparación.
 * Cada método público corre en UNA transacción que maneja el contenedor EJB:
 * si algo falla en el medio, no se guarda nada.
 *
 * @author javierrojas
 */
@Stateless
public class OrdenFacade extends AbstractFacade<Orden> {

    @PersistenceContext(unitName = "ServicioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrdenFacade() {
        super(Orden.class);
    }

    /**
     * Órdenes con sus ítems ya cargados (JOIN FETCH), las más nuevas primero.
     * Filtros opcionales: estado y cliente (null = sin filtro).
     */
    public List<Orden> listar(String estado, Integer idcliente) {
        return em.createQuery(
                "SELECT DISTINCT o FROM Orden o LEFT JOIN FETCH o.ordenItemCollection "
                + "WHERE (:estado IS NULL OR o.estado = :estado) "
                + "  AND (:cliente IS NULL OR o.clienteIdcliente.idcliente = :cliente) "
                + "ORDER BY o.idorden DESC", Orden.class)
                .setParameter("estado", estado)
                .setParameter("cliente", idcliente)
                .getResultList();
    }

    /** Una orden con sus ítems ya cargados, para la pantalla de detalle. */
    public Orden buscarConItems(Integer idorden) {
        List<Orden> lista = em.createQuery(
                "SELECT o FROM Orden o LEFT JOIN FETCH o.ordenItemCollection WHERE o.idorden = :id",
                Orden.class)
                .setParameter("id", idorden)
                .getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** Cantidad de órdenes en cada estado: cada fila es [estado, cantidad]. */
    public List<Object[]> contarPorEstado() {
        return em.createQuery(
                "SELECT o.estado, COUNT(o) FROM Orden o GROUP BY o.estado ORDER BY o.estado",
                Object[].class)
                .getResultList();
    }

    /**
     * Agrega un ítem a la orden con el PRECIO ACTUAL del ítem.
     * Si el ítem ya estaba en la orden, suma la cantidad.
     * Devuelve false si la orden ya fue entregada (no se puede modificar).
     */
    public boolean agregarItem(Integer idorden, Integer iditem, int cantidad) {
        Orden orden = em.find(Orden.class, idorden);
        Item item = em.find(Item.class, iditem);
        if (orden == null || item == null || "ENTREGADO".equals(orden.getEstado())) {
            return false;
        }

        OrdenItemPK pk = new OrdenItemPK();
        pk.setOrdenIdorden(idorden);
        pk.setItemIditem(iditem);
        OrdenItem oi = em.find(OrdenItem.class, pk);

        if (oi == null) {                                   // el ítem no estaba: se crea la línea
            oi = new OrdenItem();
            oi.setOrdenItemPK(pk);
            oi.setOrden(orden);
            oi.setItem(item);
            oi.setCantidad(cantidad);
            oi.setPrecioUnitario(item.getPrecio());         // precio del momento
            em.persist(oi);                                 // INSERT
            orden.getOrdenItemCollection().add(oi);         // mantener la relación en memoria
        } else {                                            // ya estaba: se suma la cantidad
            oi.setCantidad(oi.getCantidad() + cantidad);    // UPDATE (objeto managed)
        }
        return true;
    }

    /** Quita un ítem de la orden (si no fue entregada). */
    public boolean quitarItem(Integer idorden, Integer iditem) {
        Orden orden = em.find(Orden.class, idorden);
        if (orden == null || "ENTREGADO".equals(orden.getEstado())) {
            return false;
        }
        OrdenItemPK pk = new OrdenItemPK();
        pk.setOrdenIdorden(idorden);
        pk.setItemIditem(iditem);
        OrdenItem oi = em.find(OrdenItem.class, pk);
        if (oi != null) {
            orden.getOrdenItemCollection().remove(oi);
            em.remove(oi);                                  // DELETE
        }
        return true;
    }

    /** Cambia el estado de la orden y sus observaciones. */
    public void cambiarEstado(Integer idorden, String estado, String observaciones) {
        Orden orden = em.find(Orden.class, idorden);
        if (orden != null) {
            orden.setEstado(estado);                        // objeto managed: el UPDATE es automático
            orden.setObservaciones(observaciones);
        }
    }
}
