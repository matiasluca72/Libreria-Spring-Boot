package libreria.spring.LibreriaSpring.repositorios;

import libreria.spring.LibreriaSpring.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Matias Luca Soto
 */
@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, String> {

    @Query("SELECT c FROM Cliente c WHERE c.dni = :dni")
    public Cliente buscarPorDni(@Param("dni") Long dni);
}
