package libreria.spring.LibreriaSpring.excepciones;

/**
 *
 * @author Matias Luca Soto
 */
public class AutorServiceException extends Exception {

    /**
     * Creates a new instance of <code>AutorServiceException</code> without detail message.
     */
    public AutorServiceException() {
    }

    /**
     * Constructs an instance of <code>AutorServiceException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public AutorServiceException(String msg) {
        super(msg);
    }
}
