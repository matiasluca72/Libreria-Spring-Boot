package libreria.spring.LibreriaSpring.repositorios;

import java.util.List;
import libreria.spring.LibreriaSpring.entidades.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Matias Luca Soto
 */
@Repository
public interface PrestamoRepositorio extends JpaRepository<Prestamo, String> {

    @Query("SELECT p FROM Prestamo p WHERE p.cliente.dni = :dni")
    public List<Prestamo> buscarPorDniCliente(@Param("dni") Long dni);
    
}
