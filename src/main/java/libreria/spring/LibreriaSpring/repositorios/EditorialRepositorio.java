package libreria.spring.LibreriaSpring.repositorios;

import libreria.spring.LibreriaSpring.entidades.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Interface Repositorio de la Clase Edtiorial para persistencia en Base de datos.
 * @author Matias Luca Soto
 */
@Repository
public interface EditorialRepositorio extends JpaRepository<Editorial, String> {

    /**
     * Query personalizada para traer una Editorial según su atributo 'nombre'
     * @param nombre Parámetro de búsqueda
     * @return Editorial que coincida con el argumento enviado
     */
    @Query("SELECT e FROM Editorial e WHERE e.nombre = :nombre")
    public Editorial buscarPorNombre(@Param("nombre") String nombre);

}
