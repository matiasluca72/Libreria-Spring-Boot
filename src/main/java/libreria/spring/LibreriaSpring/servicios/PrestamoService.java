package libreria.spring.LibreriaSpring.servicios;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import libreria.spring.LibreriaSpring.entidades.Cliente;
import libreria.spring.LibreriaSpring.entidades.Libro;
import libreria.spring.LibreriaSpring.entidades.Prestamo;
import libreria.spring.LibreriaSpring.excepciones.ClienteServiceException;
import libreria.spring.LibreriaSpring.excepciones.LibroServiceException;
import libreria.spring.LibreriaSpring.excepciones.PrestamoServiceException;
import libreria.spring.LibreriaSpring.repositorios.ClienteRepositorio;
import libreria.spring.LibreriaSpring.repositorios.LibroRepositorio;
import libreria.spring.LibreriaSpring.repositorios.PrestamoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Matias Luca Soto
 */
@Service
public class PrestamoService {

    //ATRIBUTOS REPOSITORIOS
    @Autowired
    private PrestamoRepositorio prestamoRepositorio;
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;

    //ATRIBUTOS SERVICES
    @Autowired
    private LibroService libroService;
    @Autowired
    private ClienteService clienteService;

    /**
     * Método para crear una nueva instancia de la entidad Prestamo, con los id del Libro y Cliente que conformarán los atributos de esta instancia
     *
     * @param idLibro Atributo de la nueva instancia
     * @param idCliente Atributo de la nueva instancia
     * @throws LibroServiceException Si no se encuentra el Libro solicitado
     * @throws ClienteServiceException Si no se encuentra el Cliente solicitado
     * @throws PrestamoServiceException Si el préstamo no puede efectuarse por falta de ejemplares o algún otro error
     */
    @Transactional
    public void crearPrestamo(String idLibro, String idCliente) throws LibroServiceException, ClienteServiceException, PrestamoServiceException {

        //Traemos al Libro que se va a prestar y el Cliente que lo solicita
        Libro libro = libroService.buscarPorId(idLibro);
        Cliente cliente = clienteService.buscarPorId(idCliente);

        //Si ambos son valores válidos, se crea el Prestamo y se setean los atributos para persistirlo
        Prestamo prestamo = new Prestamo();
        prestamo.setFechaPrestamo(new Date());
        prestamo.setFechaDevolucion(null);
        prestamo.setAlta(true);

        //Verificamos que el Libro y el Cliente sean válidos, actualizamos los datos de ambos objetos y lo seteamos en el prestamo
        prestamo = verificarSettear(prestamo, libro, cliente);

        //Persistimos el nuevo prestamo
        prestamoRepositorio.save(prestamo);
    }

    /**
     * Método para actualizar los atributos de una instancia de Préstamo persistida en la base de datos
     *
     * @param idPrestamo De la instancia a buscar y modificar
     * @param idLibro Atributo actualizado de la instancia
     * @param idCliente Atributo actualizado de la instancia
     * @throws LibroServiceException Si no se encuentra el Libro solicitado
     * @throws ClienteServiceException Si no se encuentra el Cliente solicitado
     * @throws PrestamoServiceException Si no quedan más ejemplares o no hay cambios con respecto a los valores previos
     */
    @Transactional
    public void modificarPrestamo(String idPrestamo, String idLibro, String idCliente) throws LibroServiceException, ClienteServiceException, PrestamoServiceException {

        //Traemos al nuevo Libro que se va a prestar y al nuevo Cliente que lo solicita
        Libro libroNuevo = libroService.buscarPorId(idLibro);
        Cliente clienteNuevo = clienteService.buscarPorId(idCliente);

        //Traemos al prestamo en cuestión a modificar
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(idPrestamo);
        if (respuesta.isPresent()) {

            //Prestamo a modificar
            Prestamo prestamo = respuesta.get();

            //Traemos tambien el Libro y el Cliente anterior que ya estaban guardados en el prestamo
            Libro libroViejo = prestamo.getLibro();
            Cliente clienteViejo = prestamo.getCliente();

            //Verificamos y ejecutamos las modificaciones
            prestamo = verificarModificacion(prestamo, libroNuevo, clienteNuevo, libroViejo, clienteViejo);

            //Persistimos el prestamo actualizado
            prestamoRepositorio.save(prestamo);

        } else {
            throw new PrestamoServiceException("No se ha encontrado el prestamo especificado.");
        }
    }

    /**
     * Método para dar de baja una instancia de Prestamo en la base de datos. De esta forma, se efectua la devolución del préstamos.
     *
     * @param id Del Préstamo a dar de baja
     * @throws PrestamoServiceException Si no se encuentra el prestamo en cuestión
     */
    @Transactional
    public void darBaja(String id) throws PrestamoServiceException {

        //Buscamos el prestamo por id y nos aseguramos de que exista
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        if (respuesta.isPresent()) {

            //Devolvemos el prestamo usando otro método y persistimos los cambios
            Prestamo prestamo = devolverPrestamo(respuesta.get());
            prestamoRepositorio.save(prestamo);

        } else {
            throw new PrestamoServiceException("No se ha encontrado el prestamo solicitado.");
        }
    }

    /**
     * Método para dar de alta una instancia de Prestamo en la base de datos. De esta forma, se deshace la fecha de devolución del préstamo y se reactiva como pendiente de devolver.
     *
     * @param id Del préstamo a dar de alta
     * @throws PrestamoServiceException Si no se encuentra el prestamo en cuestión
     */
    @Transactional
    public void darAlta(String id) throws PrestamoServiceException {

        //Buscamos el prestamo por id y nos aseguramos de que exista
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        if (respuesta.isPresent()) {

            //Reactivamos el prestamo usando otro método y persistimos los cambios
            Prestamo prestamo = reactivarPrestamo(respuesta.get());
            prestamoRepositorio.save(prestamo);

        } else {
            throw new PrestamoServiceException("No se ha encontrado el prestamo solicitado.");
        }
    }

    /**
     * Busca y devuelve una instancia de Prestamo correspondiente al id pasado como parámetro
     *
     * @param id De la instancia a buscar y devolver
     * @return La instancia de Prestamo correspondiente al id
     * @throws PrestamoServiceException Si no se encuentra ninguna instancia
     */
    @Transactional(readOnly = true)
    public Prestamo buscarPorId(String id) throws PrestamoServiceException {
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new PrestamoServiceException("No se ha encontrado el prestamo solicitado.");
        }
    }

    /**
     * Devuelve una lista con todos los Prestamos en la base de datos, sin discriminar
     *
     * @return Una List con todas las instancias de Prestamos en la base de datos
     * @throws PrestamoServiceException Si hubo algún problema con la base de datos
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarTodos() throws PrestamoServiceException {
        try {
            return prestamoRepositorio.findAll();
        } catch (Exception e) {
            throw new PrestamoServiceException("Hubo un problema para traer todos los prestamos.");
        }
    }

    /**
     * Método que verifica que el Libro y Cliente existan, resta 1 libro restante y suma 1 de los prestados en los atributos del Libro, setea un nuevo prestamo y cantidad de prestamos en el Cliente, guarda ambos datos actualizados en la base de datos y lo setean en el prestamo para ser finalmente devuelto por el método.
     *
     * @param prestamo Donde setear los detalles del nuevo prestamo
     * @param libro Libro que se va a prestar (debe tener al menos 1 ejemplar restante)
     * @param cliente Que recibe el prestamo
     * @return Prestamo con el Libro y el Cliente setados
     * @throws PrestamoServiceException Si el Libro y/o Cliente son null o si Libro no cuenta con ejemplares disponibles
     */
    @Transactional
    private Prestamo verificarSettear(Prestamo prestamo, Libro libro, Cliente cliente) throws PrestamoServiceException {

        //Verificamos primero que el Libro y el Cliente tengan valores válidos y no estén vacíos
        if (libro == null) {
            throw new PrestamoServiceException("No se ha encontrado el libro especificado.");
        }
        if (cliente == null) {
            throw new PrestamoServiceException("No se ha encontrado el cliente especificado.");
        }

        //Verificamos que existan ejemplares disponibles para realizar el prestamo
        if (libro.getEjemplaresRestantes() > 0) {
            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + 1);
            libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() - 1);
            prestamo.setLibro(libro);
            libroRepositorio.save(libro);
        } else {
            throw new PrestamoServiceException("No quedan más ejemplares disponibles de este libro.");
        }

        //Seteamos el nuevo Cliente al prestamo y actualizamos la cantidad de prestamos activos de ese cliente
        cliente.setCantidadPrestamos(cliente.getCantidadPrestamos() + 1);

        //Seteamos en el Prestamo el Cliente anexado y persistimos el nuevo atributo del mismo
        prestamo.setCliente(cliente);
        clienteRepositorio.save(cliente);

        //Devolvemos el prestamo ya seteado
        return prestamo;
    }

    /**
     * Método para verificar los parámetros recibidos, verificar que es posible realizar un Préstamo y actualizar los atributos de las 5 instancias pasadas como argumentos, persistiendo a los cambios producidos en las instancias Libro y Cliente
     *
     * @param prestamo Donde guardar los valores actualizados
     * @param libroNuevo Nuevo atributo de la instancia
     * @param clienteNuevo Nuevo atributo de la instancia
     * @param libroViejo Atributo anterior de la instancia
     * @param clienteViejo Atributo anterior de la instancia
     * @return La instancia de Prestamo con sus atributos actualizados
     * @throws PrestamoServiceException Si no existen ejemplares restantes del libroNuevo o no se registraron cambios
     */
    @Transactional
    private Prestamo verificarModificacion(Prestamo prestamo, Libro libroNuevo, Cliente clienteNuevo, Libro libroViejo, Cliente clienteViejo) throws PrestamoServiceException {

        //Booleans para determinar si hubo al menos un cambio en la entidad
        boolean sameBook = false;
        boolean sameClient = false;

        //Verificamos primero que el Libro y el Cliente tengan valores válidos y no estén vacíos
        if (libroNuevo == null) {
            throw new PrestamoServiceException("No se ha encontrado el libro especificado.");
        }
        if (clienteNuevo == null) {
            throw new PrestamoServiceException("No se ha encontrado el cliente especificado.");
        }

        //Verificamos que el libro nuevo no sea el mismo que el viejo para efectuar los cambios
        if (!(libroNuevo.equals(libroViejo))) {

            //Chequeamos si el prestamo está aún activo para efectuar cambios en el libro. Sino, no se cambia nada en los libros.
            if (prestamo.getAlta()) {

                //Verificamos que existan ejemplares disponibles del Libro nuevo para prestar si el prestamo siguiese activo
                if (libroNuevo.getEjemplaresRestantes() < 1) {
                    throw new PrestamoServiceException("No quedan más ejemplares disponibles de este libro.");
                }

                //Sumamos +1 a los prestados del libro nuevo y le restamos -1 a los restantes
                libroNuevo.setEjemplaresPrestados(libroNuevo.getEjemplaresPrestados() + 1);
                libroNuevo.setEjemplaresRestantes(libroNuevo.getEjemplaresRestantes() - 1);

                //Lógica inversa para el libro viejo
                libroViejo.setEjemplaresPrestados(libroViejo.getEjemplaresPrestados() - 1);
                libroViejo.setEjemplaresRestantes(libroViejo.getEjemplaresRestantes() + 1);

                //Persistimos los cambios en la base de datos
                libroRepositorio.save(libroNuevo);
                libroRepositorio.save(libroViejo);
            }

            //Seteamos el nuevo libro en el prestamo
            prestamo.setLibro(libroNuevo);

        } else {
            sameBook = true;
        }

        //Verificamos si son clientes diferentes
        if (!(clienteNuevo.equals(clienteViejo))) {

            //Chequeamos si el prestamo está aún activo para efectuar cambios en el clientes. Sino, no se cambia nada en los clientes.
            if (prestamo.getAlta()) {

                //Actualizamos la cantidad de prestamos activos que tiene cada uno
                clienteNuevo.setCantidadPrestamos(clienteNuevo.getCantidadPrestamos() + 1);
                clienteViejo.setCantidadPrestamos(clienteViejo.getCantidadPrestamos() - 1);

                //Persistimos los cambios en ambos clientes
                clienteRepositorio.save(clienteNuevo);
                clienteRepositorio.save(clienteViejo);
            }

            //Seteamos el nuevo cliente al prestamo
            prestamo.setCliente(clienteNuevo);

        } else {
            sameClient = true;
        }

        //Si ambos atributos siguen siendo los mismos, se avisa de que no se encontraron cambios
        if (sameBook && sameClient) {
            throw new PrestamoServiceException("No se han registrado cambios.");
        }

        //Si todo salió bien, devolvemos el prestamo con sus atributos actualizados
        return prestamo;
    }

    /**
     * Método para hacer efectiva la devolución de un ejemplar de una instancia de Libro. Setea y persiste los cambios en el Libro y Cliente anexados a este préstamo
     *
     * @param prestamo Instancia a la que se dá de baja
     * @return El mismo argumento recibido con la baja y fecha de devolución setteadas
     */
    @Transactional
    private Prestamo devolverPrestamo(Prestamo prestamo) {

        //Seteamos los nuevos atributos al prestamo...
        prestamo.setFechaDevolucion(new Date());
        prestamo.setAlta(false);

        //Traemos al Libro y al Cliente de este prestamo
        Libro libro = prestamo.getLibro();
        Cliente cliente = prestamo.getCliente();

        //Devolvemos el Libro a la Libreria...
        libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() - 1);
        libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() + 1);

        //Actualizamos la cantidad de prestamos activos del cliente...
        cliente.setCantidadPrestamos(cliente.getCantidadPrestamos() - 1);

        //Y persistimos todos los cambios en Libro y Cliente
        libroRepositorio.save(libro);
        clienteRepositorio.save(cliente);

        //Devolvemos el prestamo con la fecha de devolución seteada y dado de baja
        return prestamo;
    }

    /**
     * Método que efectua la reactivación de un Prestamo ya creado y devuelto si y solo si aún quedan ejemplares restantes en el Libro guardado en este préstamo. Si es posible realizar la reactivación, se setteará en null el atributo de fechaDevolucion del Prestamo
     *
     * @param prestamo A dar la reactivación
     * @return El mismo prestamo resetteado para estar de alta
     * @throws PrestamoServiceException Si ya no quedan ejemplares restantes del Libro guardado en el Prestamo
     */
    @Transactional
    private Prestamo reactivarPrestamo(Prestamo prestamo) throws PrestamoServiceException {

        //Traemos al Libro y al Cliente de este prestamo
        Libro libro = prestamo.getLibro();
        Cliente cliente = prestamo.getCliente();

        //Verificamos que existan ejemplares disponibles para reactivar el prestamo
        if (libro.getEjemplaresRestantes() > 0) {
            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + 1);
            libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() - 1);
        } else {
            throw new PrestamoServiceException("No quedan más ejemplares disponibles de este libro actualmente.");
        }

        //Seteamos devuelta el Libro en la lista de prestamos del Cliente y actualizamos su total de prestamos activos
        cliente.setCantidadPrestamos(cliente.getCantidadPrestamos() + 1);

        //Y persistimos todos los cambios en Libro y Cliente
        libroRepositorio.save(libro);
        clienteRepositorio.save(cliente);

        //Seteamos los nuevos atributos al prestamo...
        prestamo.setFechaDevolucion(null);
        prestamo.setAlta(true);

        //Devolvemos el prestamo con la fecha de devolución en null y dado de alta
        return prestamo;
    }
}
