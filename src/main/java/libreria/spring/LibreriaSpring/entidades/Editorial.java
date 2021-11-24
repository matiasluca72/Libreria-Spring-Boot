package libreria.spring.LibreriaSpring.entidades;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.GenericGenerator;

/**
 * Una editorial posee un único atributo que es su nombre (además de su id único y su boolean alta que determina si está disponible o no). Está ligada a una lista de libros, siendo que una editorial puede pertenecer a varios libros (relación @OneToMany)
 *
 * @author Matias Luca Soto
 */
@Entity
public class Editorial {

    //ATRIBUTOS
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String nombre;
    private Boolean alta;

    //RELACIONES
    @OneToMany(mappedBy = "editorial")
    private List<Libro> libros;

    //GETTERS AND SETTERS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

}
