package libreria.spring.LibreriaSpring.repositorios;

import libreria.spring.LibreriaSpring.entidades.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Interface Repositorio de la Clase Libro para persistencia en Base de datos.
 * @author Matias Luca Soto
 */
@Repository
public interface LibroRepositorio extends JpaRepository<Libro, String> {

    /**
     * Busca un libro en la base de datos por el atributo 'ISBN'
     *
     * @param isbn Parámetro de búsqueda
     * @return Libro que coincida con el número de ISBN
     */
    @Query("SELECT l FROM Libro l WHERE l.isbn = :isbn")
    public Libro buscarPorIsbn(@Param("isbn") Long isbn);

    /**
     * Busca un Libro en la base de datos según su atributo 'título'
     * @param titulo Parámetro de búsqueda
     * @return Libro que coincida con el título enviado
     */
    @Query("SELECT l FROM Libro l WHERE l.titulo = :titulo")
    public Libro buscarPorTitulo(@Param("titulo") String titulo);

}
