package libreria.spring.LibreriaSpring.controladores;

import java.util.List;
import libreria.spring.LibreriaSpring.entidades.Autor;
import libreria.spring.LibreriaSpring.entidades.Editorial;
import libreria.spring.LibreriaSpring.entidades.Libro;
import libreria.spring.LibreriaSpring.excepciones.AutorServiceException;
import libreria.spring.LibreriaSpring.excepciones.EditorialServiceException;
import libreria.spring.LibreriaSpring.excepciones.LibroServiceException;
import libreria.spring.LibreriaSpring.servicios.AutorService;
import libreria.spring.LibreriaSpring.servicios.EditorialService;
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
 * Controlador de la Entidad Libro
 *
 * @author Matias Luca Soto
 */
@Controller
@RequestMapping("/libros")
public class LibroController {

    //ATRIBUTOS - SERVICES
    @Autowired
    private LibroService libroService;
    @Autowired
    private AutorService autorService;
    @Autowired
    private EditorialService editorialService;

    /**
     * Controlador que se encarga de renderizar el formulario para ingresar un nuevo Libro
     *
     * @param modelo De la vista
     * @return Vista con el formulario para ingresar un nuevo Libro
     */
    @GetMapping("/nuevo_libro")
    public String nuevo_libro(ModelMap modelo) {

        try {
            //Se traen y se inyectan el listado de Autores y Editoriales activos para poder seleccionarlos en el tag <select> de la vista
            List<Autor> autores = autorService.listarActivos();
            List<Editorial> editoriales = editorialService.listarActivos();
            modelo.put("autores", autores);
            modelo.put("editoriales", editoriales);
        } catch (AutorServiceException | EditorialServiceException e) {
            modelo.put("error", "Hubo un problema: " + e.getMessage());
        }
        return "libros/nuevo_libro";
    }

    /**
     * Controlador POST para recibir todos los argumentos ingresados e intentar persistir la nueva instancia de Libro
     *
     * @param modelo ModelMap de la vista
     * @param titulo Atributo actualizado de la instancia
     * @param isbn Atributo actualizado de la instancia
     * @param idAutor Atributo actualizado de la instancia
     * @param idEditorial Atributo actualizado de la instancia
     * @param anio Atributo actualizado de la instancia
     * @param ejemplares Atributo actualizado de la instancia
     * @return Formulario de Nuevo Libro con un mensaje de éxito o de error inyectado
     */
    @PostMapping("/nuevo_libro")
    public String guardar_libro(ModelMap modelo, @RequestParam String titulo, @RequestParam Long isbn, @RequestParam String idAutor, @RequestParam String idEditorial, @RequestParam Integer anio, @RequestParam Integer ejemplares) {

        try {

            //Intentamos crear el Libro y persistirlo utilizando un método de la Clase Service
            libroService.crearLibro(isbn, titulo, anio, ejemplares, idAutor, idEditorial);
            modelo.put("exito", "¡Libro añadido con éxito!");

        } catch (AutorServiceException | EditorialServiceException | LibroServiceException e) {

            //Si algo sale mal, inyectamos el mensaje de error y los valores recién añadidos para mejorar el UX
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            modelo.put("titulo", titulo);
            modelo.put("isbn", isbn);
            modelo.put("nombreAutor", idAutor);
            modelo.put("nombreEditorial", idEditorial);
            modelo.put("anio", anio);
            modelo.put("ejemplares", ejemplares);
        }
        return nuevo_libro(modelo);

    }

    /**
     * Controlador que devuelve la vista con el listado de todos los libros guardados en la base de datos
     *
     * @param modelo De la vista
     * @return la vista con el listado de todos los libros guardados en la base de datos
     */
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

    /**
     * Controlador para mostrar el fomulario de Modificar Libro con los valores actuales de la instancia inyectados
     *
     * @param id De la instancia a modificar
     * @param modelo de la vista
     * @return El formulario para Modificar un Libro
     * @throws AutorServiceException Si surge algún problema trayendo al listado de entidades
     * @throws EditorialServiceException Si surge algún problema trayendo al listado de entidades
     */
    @GetMapping("/modificar/{id}")
    public String modificarLibro(@PathVariable String id, ModelMap modelo) throws AutorServiceException, EditorialServiceException {

        try {
            //Traemos el listado de Autores y Editoriales para inyectarlos en el tag <select> del HTML
            List<Autor> autores = autorService.listarActivos();
            List<Editorial> editoriales = editorialService.listarActivos();
            modelo.put("autores", autores);
            modelo.put("editoriales", editoriales);

            //Inyectamos el Libro a modificar con sus atributos actuales
            modelo.put("libro", libroService.buscarPorId(id));

        } catch (LibroServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "libros/modificar_libro";
    }

    /**
     * Controlador POST para recibir los argumentos recibidos desde el formulario e intentar la persistencia de los nuevos valores en la base de datos
     *
     * @param id De la entidad a modificar
     * @param modelo de la vista
     * @param titulo Atributo actualizado de la entidad
     * @param isbn Atributo actualizado de la entidad
     * @param idAutor Atributo actualizado de la entidad
     * @param idEditorial Atributo actualizado de la entidad
     * @param anio Atributo actualizado de la entidad
     * @param ejemplares Atributo actualizado de la entidad
     * @return Si sale todo bien, el listado de Libros actualizado. Si algo sale mal, el formulario de Modificar Libro nuevamente
     * @throws AutorServiceException Si surge algún problema trayendo los listados de las entidades
     * @throws EditorialServiceException Si surge algún problema trayendo los listados de las entidades
     */
    @PostMapping("/modificar/{id}")
    public String guardarModificacion(@PathVariable String id, ModelMap modelo, @RequestParam String titulo, @RequestParam Long isbn, @RequestParam String idAutor, @RequestParam String idEditorial, @RequestParam Integer anio, @RequestParam Integer ejemplares) throws AutorServiceException, EditorialServiceException {

        try {
            libroService.modificarLibro(id, isbn, titulo, anio, ejemplares, idAutor, idEditorial);
            modelo.put("exito", "¡Libro modificado con éxito!");
            return listado_libros(modelo);
        } catch (AutorServiceException | EditorialServiceException | LibroServiceException e) {
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return modificarLibro(id, modelo);
        }
    }

    /**
     * Controlador para dar de baja a una instancia en particular y devolver la vista con el listado de Libros
     *
     * @param id De la instancia a dar de baja
     * @param modelo
     * @return Listado de Libros actualizado o con un mensaje de error inyectado
     */
    @GetMapping("/baja/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {

        try {
            libroService.darBaja(id);
            return "redirect:/libros/listado_libros";
        } catch (LibroServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
            return listado_libros(modelo);
        }
    }

    /**
     * Controlador para dar de alta a una instancia en particular y devolver la vista con el listado de Libros
     *
     * @param id De la instancia a dar de alta
     * @param modelo
     * @return Listado de Libros actualizado o con un mensaje de error inyectado
     */
    @GetMapping("/alta/{id}")
    public String darAlta(@PathVariable String id, ModelMap modelo) {

        try {
            libroService.darAlta(id);
            return "redirect:/libros/listado_libros";
        } catch (LibroServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
            return listado_libros(modelo);
        }
    }
}
