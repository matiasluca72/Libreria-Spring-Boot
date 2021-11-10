package libreria.spring.LibreriaSpring.controladores;

import libreria.spring.LibreriaSpring.servicios.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
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

        } catch (Exception e) {
            
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
    public String listado_libros() {
        return "libros/listado_libros.html";
    }
}
