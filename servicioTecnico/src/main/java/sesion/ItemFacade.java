package sesion;

import entidad.Item;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author javierrojas
 */
@Stateless
public class ItemFacade extends AbstractFacade<Item> {

    @PersistenceContext(unitName = "ServicioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemFacade() {
        super(Item.class);
    }

    /** Ítems ordenados por descripción. */
    public List<Item> listarOrdenados() {
        return em.createQuery("SELECT i FROM Item i ORDER BY i.descripcion", Item.class)
                .getResultList();
    }

    /** ¿En cuántas órdenes se usó este ítem? Si se usó, no se puede borrar. */
    public long cantidadUsos(Integer iditem) {
        return em.createQuery(
                "SELECT COUNT(oi) FROM OrdenItem oi WHERE oi.item.iditem = :id", Long.class)
                .setParameter("id", iditem)
                .getSingleResult();
    }
}
