package libreria.spring.LibreriaSpring.servicios;

import java.util.Date;
import java.util.Iterator;
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

    //ATRIBUTOS SERVICES
    @Autowired
    private LibroService libroService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;

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

    @Transactional(readOnly = true)
    public Prestamo buscarPorId(String id) throws PrestamoServiceException {
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new PrestamoServiceException("No se ha encontrado el prestamo solicitado.");
        }
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarTodos() throws PrestamoServiceException {
        try {
            return prestamoRepositorio.findAll();
        } catch (Exception e) {
            throw new PrestamoServiceException("Hubo un problam para traer todos los prestamos.");
        }
    }

    /**
     * Método que verifica que el Libro y Cliente existan, resta 1 libro restando y suma 1 de los prestados en los atributos del Libro, setea un nuevo prestamo y cantidad de prestamos en el Cliente, guarda ambos datos actualizados en la base de datos y lo setean en el prestamo para ser finalmente devuelto por el método.
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

        prestamo.setCliente(cliente);
        clienteRepositorio.save(cliente);

        return prestamo;
    }

    @Transactional
    private Prestamo verificarModificacion(Prestamo prestamo, Libro libroNuevo, Cliente clienteNuevo, Libro libroViejo, Cliente clienteViejo) throws PrestamoServiceException {

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
        if (sameBook && sameClient) {
            throw new PrestamoServiceException("No se han registrado cambios.");
        }
        return prestamo;
    }

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

    @Transactional
    private Prestamo reactivarPrestamo(Prestamo prestamo) throws PrestamoServiceException {

        //Seteamos los nuevos atributos al prestamo...
        prestamo.setFechaDevolucion(null);
        prestamo.setAlta(true);

        //Traemos al Libro y al Cliente de este prestamo
        Libro libro = prestamo.getLibro();
        Cliente cliente = prestamo.getCliente();

        //Verificamos que existan ejemplares disponibles para reactivar el prestamo
        if (libro.getEjemplaresRestantes() > 0) {
            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + 1);
            libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() - 1);
        } else {
            throw new PrestamoServiceException("No quedan más ejemplares disponibles de este libro.");
        }

        //Seteamos devuelta el Libro en la lista de prestamos del Cliente y actualizamos su total de prestamos activos
        cliente.setCantidadPrestamos(cliente.getCantidadPrestamos() + 1);

        //Y persistimos todos los cambios en Libro y Cliente
        libroRepositorio.save(libro);
        clienteRepositorio.save(cliente);

        //Devolvemos el prestamo con la fecha de devolución en null y dado de alta
        return prestamo;
    }
}
