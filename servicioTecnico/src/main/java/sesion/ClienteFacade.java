package sesion;

import entidad.Cliente;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author javierrojas
 */
@Stateless
public class ClienteFacade extends AbstractFacade<Cliente> {

    @PersistenceContext(unitName = "ServicioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClienteFacade() {
        super(Cliente.class);
    }

    /**
     * Busca clientes por apellido, nombre, teléfono o número de cliente.
     * Si el texto está vacío, devuelve todos. Ordenados por apellido y nombre.
     */
    public List<Cliente> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return em.createQuery("SELECT c FROM Cliente c ORDER BY c.apellido, c.nombre", Cliente.class)
                    .getResultList();
        }
        String t = texto.trim();
        Integer numero = t.matches("\\d+") ? Integer.valueOf(t) : -1;   // ¿buscó por número?
        return em.createQuery(
                "SELECT c FROM Cliente c "
                + "WHERE LOWER(c.apellido) LIKE :q OR LOWER(c.nombre) LIKE :q "
                + "   OR c.telefono LIKE :q OR c.idcliente = :numero "
                + "ORDER BY c.apellido, c.nombre", Cliente.class)
                .setParameter("q", "%" + t.toLowerCase() + "%")
                .setParameter("numero", numero)
                .getResultList();
    }

    /** ¿Cuántas órdenes tiene el cliente? Si tiene, no se puede borrar. */
    public long cantidadOrdenes(Integer idcliente) {
        return em.createQuery(
                "SELECT COUNT(o) FROM Orden o WHERE o.clienteIdcliente.idcliente = :id", Long.class)
                .setParameter("id", idcliente)
                .getSingleResult();
    }
}
