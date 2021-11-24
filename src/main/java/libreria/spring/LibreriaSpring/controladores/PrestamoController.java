package libreria.spring.LibreriaSpring.controladores;

import java.util.List;
import libreria.spring.LibreriaSpring.entidades.Cliente;
import libreria.spring.LibreriaSpring.entidades.Libro;
import libreria.spring.LibreriaSpring.entidades.Prestamo;
import libreria.spring.LibreriaSpring.excepciones.ClienteServiceException;
import libreria.spring.LibreriaSpring.excepciones.LibroServiceException;
import libreria.spring.LibreriaSpring.excepciones.PrestamoServiceException;
import libreria.spring.LibreriaSpring.servicios.ClienteService;
import libreria.spring.LibreriaSpring.servicios.LibroService;
import libreria.spring.LibreriaSpring.servicios.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de la Clase Prestamo
 *
 * @author Matias Luca Soto
 */
@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    //ATRIBUTOS SERVICES
    @Autowired
    private PrestamoService prestamoService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private LibroService libroService;

    /**
     * Controlador del formulario para efectuar un nuevo prestamo y persistirlo en la base de datos
     *
     * @param modelo
     * @return Formulario para ingresar un nuevo préstamo
     */
    @GetMapping("/nuevo_prestamo")
    public String nuevo_prestamo(ModelMap modelo) {
        try {
            //Traemos e inyectamos dos listados con los Libros y Clientes activos para settearlos en el nuevo prestamo
            List<Cliente> clientes = clienteService.listarActivos();
            List<Libro> libros = libroService.listarActivos();
            modelo.put("clientes", clientes);
            modelo.put("libros", libros);
        } catch (ClienteServiceException | LibroServiceException e) {
            modelo.put("error", "Hubo un problema: " + e.getMessage());
        }
        return "prestamos/nuevo_prestamo";
    }

    /**
     * Controlador POST que recibe los argumentos ingresados e intenta persistir la nueva instancia en la base de datos
     *
     * @param modelo
     * @param idLibro Atributos de la nueva instancia
     * @param idCliente Atributos de la nueva instancia
     * @return formulario de nuevo prestamo con un mensaje de éxito o error inyectado
     */
    @PostMapping("/nuevo_prestamo")
    public String guardar_prestamo(ModelMap modelo, @RequestParam String idLibro, @RequestParam String idCliente) {

        try {
            //Intetnamos persistir el nuevo Objeto usando un método de la Clase Service
            prestamoService.crearPrestamo(idLibro, idCliente);
            modelo.put("exito", "¡Prestamo efectuado!");

        } catch (ClienteServiceException | LibroServiceException | PrestamoServiceException e) {
            //Inyección del mensaje de error
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
        }
        return nuevo_prestamo(modelo);
    }

    /**
     * Controlador que devuelve la vista con el listado con todos los prestamos
     *
     * @param modelo
     * @return la vista con el listado con todos los prestamos
     */
    @GetMapping("/listado_prestamos")
    public String listado_prestamos(ModelMap modelo) {

        try {
            //Inyección del listado con todos los Prestamos dentro del MOdelMap
            List<Prestamo> prestamos = prestamoService.listarTodos();
            modelo.addAttribute("prestamos", prestamos);
        } catch (PrestamoServiceException e) {
            modelo.put("error", "Hubo un problema: " + e.getMessage());
        }
        return "prestamos/listado_prestamos";
    }

    /**
     * Controlador con el formulario de Modificar Prestamo
     *
     * @param id De la instancia a modificar
     * @param modelo
     * @return Formulario para modificar los atributos de una instancia de Prestamo
     * @throws ClienteServiceException Si hay algún problema trayendo a las instancias
     * @throws LibroServiceException Si hay algún problema trayendo a las instancias
     */
    @GetMapping("/modificar/{id}")
    public String modificarPrestamo(@PathVariable String id, ModelMap modelo) throws ClienteServiceException, LibroServiceException {

        try {
            //Se lista y se inyectan los Clientes y Libros activos para ser seleccionados en el tag <select> del HTML,
            //junto con el Objeto Prestamo a modificar para presetear los valores actuales de este
            List<Cliente> clientes = clienteService.listarActivos();
            List<Libro> libros = libroService.listarActivos();
            modelo.put("clientes", clientes);
            modelo.put("libros", libros);
            modelo.put("prestamo", prestamoService.buscarPorId(id));
        } catch (PrestamoServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "prestamos/modificar_prestamo";
    }

    /**
     * Controlador POST para recibir los argumentos ingresados en el formulario de Modificar Prestamo e intentar persistir los cambios en la base de datos
     *
     * @param id De la instancia a modificar
     * @param modelo
     * @param idLibro Atributos actualizados de la instancia Prestamo
     * @param idCliente Atributos actualizados de la instancia Prestamo
     * @return Si todo sale bien, el listado con los prestamos actualizados. Sino, el formulario de modificación con un mensaje de error inyectado
     * @throws ClienteServiceException Si surge algun problema buscando a la entidad
     * @throws LibroServiceException Si surge algun problema buscando a la entidad
     */
    @PostMapping("/modificar/{id}")
    public String guardarModificacion(@PathVariable String id, ModelMap modelo, @RequestParam String idLibro, @RequestParam String idCliente) throws ClienteServiceException, LibroServiceException {

        try {
            prestamoService.modificarPrestamo(id, idLibro, idCliente);
            modelo.put("exito", "¡Prestamo actualizado!");
            return listado_prestamos(modelo);
        } catch (ClienteServiceException | LibroServiceException | PrestamoServiceException e) {
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return modificarPrestamo(id, modelo);
        }
    }

    /**
     * Controlador para dar de baja una entidad en específico
     *
     * @param id De la entidad a dar de baja
     * @param modelo
     * @return El listado actualizado con todos los prestamos
     */
    @GetMapping("/baja/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {

        try {
            prestamoService.darBaja(id);
            return "redirect:/prestamos/listado_prestamos";
        } catch (PrestamoServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
            return listado_prestamos(modelo);
        }
    }

    /**
     * Controlador para dar de alta una entidad en específico
     *
     * @param id De la entidad a dar de alta
     * @param modelo
     * @return El listado actualizado con todos los prestamos
     */
    @GetMapping("/alta/{id}")
    public String darAlta(@PathVariable String id, ModelMap modelo) {

        try {
            prestamoService.darAlta(id);
            return "redirect:/prestamos/listado_prestamos";
        } catch (PrestamoServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
            return listado_prestamos(modelo);
        }
    }

}
