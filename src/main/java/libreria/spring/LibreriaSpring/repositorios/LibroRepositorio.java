package libreria.spring.LibreriaSpring.repositorios;

import libreria.spring.LibreriaSpring.entidades.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Matias Luca Soto
 */
@Repository
public interface LibroRepositorio extends JpaRepository<Libro, String> {

    /**
     * Busca un libro en la base de datos por el número de ISBN
     *
     * @param isbn
     * @return
     */
    @Query("SELECT l FROM Libro l WHERE l.isbn = :isbn")
    public Libro buscarPorIsbn(@Param("isbn") Long isbn);

    @Query("SELECT l FROM Libro l WHERE l.titulo = :titulo")
    public Libro buscarPorTitulo(@Param("titulo") String titulo);

}
