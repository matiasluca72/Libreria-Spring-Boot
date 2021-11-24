package libreria.spring.LibreriaSpring.repositorios;

import java.util.List;
import libreria.spring.LibreriaSpring.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Interface Repositorio de la Clase Cliente para persistencia en Base de datos.
 * @author Matias Luca Soto
 */
@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, String> {

    /**
     * Query personalizada para buscar un Cliente según su atributo 'dni'
     * @param dni Parámetro de búsqueda
     * @return Un Cliente según su dni
     */
    @Query("SELECT c FROM Cliente c WHERE c.dni = :dni")
    public Cliente buscarPorDni(@Param("dni") Long dni);
    
    /**
     * Query personalizada para buscar Clientes según su atributo 'nombre'
     * @param nombre Parámetro de búsqueda
     * @return Clientes que coincidan con el argumento
     */
    @Query("SELECT c FROM Cliente c WHERE c.nombre = :nombre")
    public List<Cliente> buscarPorNombre(@Param("nombre") String nombre);
    
    /**
     * Query personalizada para buscar Clientes según su atributo 'apellido'
     * @param apellido Parámetro de búsqueda
     * @return Clientes que coincidan con el argumento
     */
    @Query("SELECT c FROM Cliente c WHERE c.nombre = :apellido")
    public List<Cliente> buscarPorApellido(@Param("apellido") String apellido);
}
