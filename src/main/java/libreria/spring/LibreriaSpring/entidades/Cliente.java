package libreria.spring.LibreriaSpring.entidades;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.GenericGenerator;

/**
 * Un Cliente posee una serie de atributos: un id único e irrepetible, un número de documento (DNI), su nombre, apellido, un número
 * de teléfono, un booleano para darlo de alta/baja, una relación @OneToMany con la Clase Préstamos (siendo que un Cliente puede
 * efectuar varios Préstamos) mapeados desde esta entidad, y una cantidad total de préstamos activos ligados a este Cliente.
 * @author Matias Luca Soto
 */
@Entity
public class Cliente {

    //ATRIBUTOS
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private Long dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private Boolean alta;
    
    //RELACIONES
    @OneToMany(mappedBy = "cliente")
    private List<Prestamo> prestamos;
    private Integer cantidadPrestamos;
    
    //CONSTRUCTOR CON cantidadPrestamos INICIALIZADO
    public Cliente() {
        cantidadPrestamos = 0;
    }
    
    //GETTERS AND SETTERS
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the dni
     */
    public Long getDni() {
        return dni;
    }

    /**
     * @param dni the dni to set
     */
    public void setDni(Long dni) {
        this.dni = dni;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the apellido
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * @param apellido the apellido to set
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono the telefono to set
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @return the alta
     */
    public Boolean getAlta() {
        return alta;
    }

    /**
     * @param alta the alta to set
     */
    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

    /**
     * @return the prestamos
     */
    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    /**
     * @param prestamos the prestamos to set
     */
    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    /**
     * @return the cantidadPrestamos
     */
    public Integer getCantidadPrestamos() {
        return cantidadPrestamos;
    }

    /**
     * @param cantidadPrestamos the cantidadPrestamos to set
     */
    public void setCantidadPrestamos(Integer cantidadPrestamos) {
        this.cantidadPrestamos = cantidadPrestamos;
    }
}
