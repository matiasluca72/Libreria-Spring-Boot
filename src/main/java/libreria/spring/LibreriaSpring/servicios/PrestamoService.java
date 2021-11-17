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

    @Transactional
    public void crearPrestamo(String idLibro, String idCliente) throws LibroServiceException, ClienteServiceException, PrestamoServiceException {

        //Traemos al Libro que se va a prestar y el Cliente que lo solicita
        Libro libro = libroService.buscarPorId(idLibro);
        Cliente cliente = clienteService.buscarPorId(idCliente);

        //Si ambos son valores válidos, se crea el Prestamo y se setean los atributos para persistirlo
        if (libro != null && cliente != null) {

            Prestamo prestamo = new Prestamo();
            prestamo.setFechaPrestamo(new Date());
            prestamo.setFechaDevolucion(null);
            prestamo.setLibro(libro);
            prestamo.setCliente(cliente);
            prestamo.setAlta(true);

            prestamoRepositorio.save(prestamo);

        } else {
            throw new PrestamoServiceException("El libro y/o cliente no son válidos.");
        }
    }

    @Transactional
    public void modificarPrestamo(String idPrestamo, Date fechaPrestamo, Date fechaDevolucion, String idLibro, String idCliente) throws LibroServiceException, ClienteServiceException, PrestamoServiceException {

        //Traemos al nuevo Libro que se va a prestar y al nuevo Cliente que lo solicita
        Libro libro = libroService.buscarPorId(idLibro);
        Cliente cliente = clienteService.buscarPorId(idCliente);

        if (libro != null && cliente != null) {

            Optional<Prestamo> respuesta = prestamoRepositorio.findById(idPrestamo);
            if (respuesta.isPresent()) {

                Prestamo prestamo = respuesta.get();

                if (fechaPrestamo == null) {
                    throw new PrestamoServiceException("La fecha de prestamo no puede estar vacia.");
                }
                
                prestamo.setFechaPrestamo(fechaPrestamo);
                prestamo.setFechaDevolucion(fechaDevolucion);
                prestamo.setLibro(libro);
                prestamo.setCliente(cliente);
                prestamo.setAlta(true);

                prestamoRepositorio.save(prestamo);

            } else {
                throw new PrestamoServiceException("No se ha encontrado el prestamo especificado.");
            }
        } else {
            throw new PrestamoServiceException("El libro y/o cliente no son válidos.");
        }
    }

    @Transactional
    public void darBaja(String id) throws PrestamoServiceException {
        
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();
            prestamo.setFechaDevolucion(new Date());
            prestamo.setAlta(false);
        } else {
            throw new PrestamoServiceException("No se ha encontrado el prestamo solicitado.");
        }
    }
    
    @Transactional
    public void darAlta(String id) throws PrestamoServiceException {
        
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();
            prestamo.setFechaDevolucion(null);
            prestamo.setAlta(true);
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

}
