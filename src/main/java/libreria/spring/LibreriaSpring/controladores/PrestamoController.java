package libreria.spring.LibreriaSpring.controladores;

import java.util.Date;
import java.util.List;
import libreria.spring.LibreriaSpring.entidades.Prestamo;
import libreria.spring.LibreriaSpring.excepciones.ClienteServiceException;
import libreria.spring.LibreriaSpring.excepciones.LibroServiceException;
import libreria.spring.LibreriaSpring.excepciones.PrestamoServiceException;
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
 *
 * @author Matias Luca Soto
 */
@Controller
@RequestMapping("/prestamo")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @GetMapping("/")
    public String prestamos() {
        return "prestamos/prestamos";
    }

    @GetMapping("/nuevo_prestamo")
    public String nuevo_prestamo() {
        return "prestamos/nuevo_prestamo";
    }

    @PostMapping("/nuevo_prestamo")
    public String guardar_prestamo(ModelMap modelo, @RequestParam String idLibro, @RequestParam String idCliente) {

        try {

            prestamoService.crearPrestamo(idLibro, idCliente);
            modelo.put("exito", "¡Prestamo efectuado!");
            return "prestamo/nuevo_prestamo";

        } catch (ClienteServiceException | LibroServiceException | PrestamoServiceException e) {

            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return "prestamos/nuevo_prestamo";

        }
    }
    
    @GetMapping("/listado_prestamos")
    public String listado_prestamos(ModelMap modelo) {
        
        try {
            List<Prestamo> prestamos = prestamoService.listarTodos();
            modelo.addAttribute("prestamos", prestamos);
        } catch (PrestamoServiceException e) {
            modelo.put("error", "Hubo un problema: " + e.getMessage());
        }
        return "prestamos/listado_prestamos";
    } 
    
    @GetMapping("/modificar/{id}")
    public String modificarPrestamo(@PathVariable String id, ModelMap modelo) {
        
        try {
            modelo.put("prestamo", prestamoService.buscarPorId(id));
        } catch (PrestamoServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "prestamos/modificar_prestamo";
    }
    
    @PostMapping("/modificar/{id}")
    public String guardarModificacion(@PathVariable String id, ModelMap modelo, @RequestParam Date fechaPrestamo, Date fechaDevolucion, @RequestParam String idLibro, @RequestParam String idCliente) {
        
        try {
            prestamoService.modificarPrestamo(id, fechaPrestamo, fechaDevolucion, idLibro, idCliente);
            modelo.put("exito", "¡Prestamo actualizado!");
            return listado_prestamos(modelo);
        } catch (ClienteServiceException | LibroServiceException | PrestamoServiceException e) {
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return modificarPrestamo(id, modelo);
        }
    }
    
    @GetMapping("/baja/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {
        
        try {
            prestamoService.darBaja(id);
        } catch (PrestamoServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "redirect:/prestamos/listado_prestamos";
    }
    
    @GetMapping("/alta/{id}")
    public String darAlta(@PathVariable String id, ModelMap modelo) {
        
        try {
            prestamoService.darAlta(id);
        } catch (PrestamoServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "redirect:/prestamos/listado_prestamos";
    }

}
