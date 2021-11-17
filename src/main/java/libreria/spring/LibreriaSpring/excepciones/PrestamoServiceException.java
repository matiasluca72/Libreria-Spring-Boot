package libreria.spring.LibreriaSpring.excepciones;

/**
 *
 * @author Matias Luca Soto
 */
public class PrestamoServiceException extends Exception {

    /**
     * Creates a new instance of <code>PrestamoServiceException</code> without detail message.
     */
    public PrestamoServiceException() {
    }


    /**
     * Constructs an instance of <code>PrestamoServiceException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PrestamoServiceException(String msg) {
        super(msg);
    }
}
