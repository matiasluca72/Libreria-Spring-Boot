package libreria.spring.LibreriaSpring.controladores;

import java.util.List;
import libreria.spring.LibreriaSpring.entidades.Autor;
import libreria.spring.LibreriaSpring.entidades.Libro;
import libreria.spring.LibreriaSpring.excepciones.AutorServiceException;
import libreria.spring.LibreriaSpring.excepciones.EditorialServiceException;
import libreria.spring.LibreriaSpring.excepciones.LibroServiceException;
import libreria.spring.LibreriaSpring.servicios.LibroService;
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
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping("/")
    public String libros() {
        return "libros/libros.html";
    }

    @GetMapping("/nuevo_libro")
    public String nuevo_libro() {
        return "libros/nuevo_libro.html";
    }

    @PostMapping("/nuevo_libro")
    public String guardar_libro(ModelMap modelo, @RequestParam String titulo, @RequestParam Long isbn, @RequestParam String nombreAutor, @RequestParam String nombreEditorial, @RequestParam Integer anio, @RequestParam Integer ejemplares, @RequestParam Integer ejemplaresPrestados) {

        try {

            libroService.crearLibro(isbn, titulo, anio, ejemplares, ejemplaresPrestados, nombreAutor, nombreEditorial);
            modelo.put("exito", "¡Libro añadido con éxito!");
            return "libros/nuevo_libro.html";

        } catch (AutorServiceException | EditorialServiceException | LibroServiceException e) {

            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            modelo.put("titulo", titulo);
            modelo.put("isbn", isbn);
            modelo.put("nombreAutor", nombreAutor);
            modelo.put("nombreEditorial", nombreEditorial);
            modelo.put("anio", anio);
            modelo.put("ejemplares", ejemplares);
            modelo.put("ejemplaresPrestados", ejemplaresPrestados);
            return "libros/nuevo_libro.html";
        }
    }

    @GetMapping("/listado_libros")
    public String listado_libros(ModelMap modelo) {

        try {
            //Traigo una lista con todos los Libros desde Service -> Repositorio
            List<Libro> libros = libroService.listarTodos();
            //Utilizo otro método de ModelMap para inyectar una key "libros" que contenga la lista
            modelo.addAttribute("libros", libros);
        } catch (LibroServiceException e) {
            modelo.put("error", "Hubo un problema: " + e.getMessage());
        }

        return "libros/listado_libros";

    }
    
    @GetMapping("/modificar/{id}")
    public String modificarLibro(@PathVariable String id, ModelMap modelo) {
        
        try {
            modelo.put("libro", libroService.buscarPorId(id));
        } catch (LibroServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "libros/modificar_libro";
    }
    
    @PostMapping("/modificar/{id}")
    public String guardarModificacion(@PathVariable String id, ModelMap modelo, @RequestParam String titulo, @RequestParam Long isbn, @RequestParam String nombreAutor, @RequestParam String nombreEditorial, @RequestParam Integer anio, @RequestParam Integer ejemplares, @RequestParam Integer ejemplaresPrestados) {
        
        try {
            libroService.modificarLibro(id, isbn, titulo, anio, ejemplares, ejemplaresPrestados, nombreAutor, nombreEditorial);
            modelo.put("exito", "¡Libro modificado con éxito!");
            return listado_libros(modelo);
        } catch (AutorServiceException | EditorialServiceException | LibroServiceException e) {
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return modificarLibro(id, modelo);
        }
    }
    
    @GetMapping("/baja/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {
        
        try {
            libroService.darBaja(id);
        } catch (LibroServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "redirect:/libros/listado_libros";
    }
    
    @GetMapping("/alta/{id}")
    public String darAlta(@PathVariable String id, ModelMap modelo) {
        
        try {
            libroService.darAlta(id);
        } catch (LibroServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "redirect:/libros/listado_libros";
    }
}
