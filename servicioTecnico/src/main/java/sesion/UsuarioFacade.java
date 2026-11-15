package sesion;

import entidad.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

/**
 *
 * @author javierrojas
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "ServicioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    /**
     * Busca un usuario por nombre de usuario y contraseña.
     * La contraseña se compara como hash SHA-256: en la base nunca se guarda en texto plano.
     * Devuelve el usuario, o null si los datos no coinciden.
     */
    public Usuario login(String usuario, String clave) {
        List<Usuario> lista = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.usuario = :usuario AND u.password = :password",
                Usuario.class)
                .setParameter("usuario", usuario)
                .setParameter("password", hash(clave))
                .getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** Calcula el hash SHA-256 en hexadecimal (igual que SHA2(texto, 256) de MySQL). */
    public static String hash(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
