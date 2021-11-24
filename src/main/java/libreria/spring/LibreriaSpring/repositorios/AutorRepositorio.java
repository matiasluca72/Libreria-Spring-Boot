package libreria.spring.LibreriaSpring.repositorios;

import libreria.spring.LibreriaSpring.entidades.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Interface Repositorio de la Clase Autor para persistencia en Base de datos.
 * @author Matias Luca Soto
 */
@Repository
public interface AutorRepositorio extends JpaRepository<Autor, String> {

    /**
     * Método abstracto para realizar una query personalizada buscando 1 Autor según su atributo 'nombre'
     * @param nombre del Autor a buscar
     * @return Autor con el nombre especificado
     */
    @Query("SELECT a FROM Autor a WHERE a.nombre = :nombre")
    public Autor buscarPorNombre(@Param("nombre") String nombre);

}
