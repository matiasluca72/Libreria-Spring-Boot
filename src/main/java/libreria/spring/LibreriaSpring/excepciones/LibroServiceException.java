package libreria.spring.LibreriaSpring.excepciones;

/**
 *
 * @author Matias Luca Soto
 */
public class LibroServiceException extends Exception {

    /**
     * Creates a new instance of <code>UsuarioServiceException</code> without detail message.
     */
    public LibroServiceException() {
    }

    /**
     * Constructs an instance of <code>UsuarioServiceException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public LibroServiceException(String msg) {
        super(msg);
    }
}
