package libreria.spring.LibreriaSpring.controladores;

import java.util.List;
import libreria.spring.LibreriaSpring.entidades.Autor;
import libreria.spring.LibreriaSpring.excepciones.AutorServiceException;
import libreria.spring.LibreriaSpring.servicios.AutorService;
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
@RequestMapping("/autores")
public class AutorController {

    //ATRIBUTOS - SERVICES
    @Autowired
    private AutorService autorService;

    /**
     * Método que devuelve el Menú Principal de Autores para poder ingresar o ver el listado de los autores existentes
     *
     * @return Menú Autores
     */
    @GetMapping("/")
    public String autores() {
        return "autores/autores.html";
    }

    /**
     * Método que devuelve el formulario vacío para poder ingresar un nuevo Autor a la base de datos
     *
     * @return Formulario para ingresar un nuevo Autor
     */
    @GetMapping("/nuevo_autor")
    public String nuevo_autor() {
        return "autores/nuevo_autor.html";
    }

    /**
     * Método que recibe los argumentos ingresados en el formulario de nuevo Autor y los persiste en la base de datos, mostrando un mensaje de confirmación si todo salió bien o uno de error si saltó alguna excepción
     *
     * @param modelo ModelMap de la vista
     * @param nombre del nuevo Autor ingresado por el Usuario
     * @return Formulario de nuevo Autor con un mensaje de confirmación o de error
     */
    @PostMapping("/nuevo_autor")
    public String guardar_autor(ModelMap modelo, @RequestParam String nombre) {

        try {

            autorService.crearNuevoAutor(nombre);
            modelo.put("exito", "¡Autor guardado con éxito!");

        } catch (Exception e) {

            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            modelo.put("nombre", nombre);
        }

        return "autores/nuevo_autor.html";
    }

    /**
     * Devuelve la vista con el listado de autores guardados en la base de datos
     *
     * @param modelo Para inyectar la lista a la vista
     * @return Lista de autores persistidos
     */
    @GetMapping("/listado_autores")
    public String listado_autores(ModelMap modelo) {

        try {
            //Traigo una lista con todos los Autores desde Service -> Repositorio
            List<Autor> autores = autorService.listarTodos();
            //Utilizo otro método de ModelMap para inyectar una key "autores" que contenga la lista
            modelo.addAttribute("autores", autores);
        } catch (Exception e) {
            System.out.println("Hubo un problema: " + e.getMessage());
        }

        return "autores/listado_autores.html";
    }

    /**
     * Método que devuelve un formulario para actualizar los atributos de un Autor ya existente
     *
     * @param id del autor a modificar - @PathVariable indica que se usará el id como parte de la URL
     * @param modelo ModelMap de la vista a mostrar
     * @return Formulario para actualizar un Autor existente
     */
    @GetMapping("/modificar/{id}") //El {id} es el que se recibe como parámetro con la anotación @PathVariable
    public String modificarAutor(@PathVariable String id, ModelMap modelo) {

        try {
            modelo.put("autor", autorService.buscarPorId(id));
        } catch (Exception e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "autores/modificar_autor.html";
    }

    @PostMapping("/modificar/{id}") //El {id} es el que se recibe como parámetro con la anotación @PathVariable
    public String guardarModificacion(@PathVariable String id, ModelMap modelo, @RequestParam String nombre) {

        try {
            autorService.modificarAutor(id, nombre);
            modelo.put("exito", "¡Autor modificado con éxito!");
            return listado_autores(modelo);

            /* Alternativa para devolver la vista, pero da errores de servidor
            List<Autor> autores = autorService.listarTodos();
            modelo.addAttribute("autores", autores);
            return "autores/listado_autores.html"; */
            
        } catch (Exception e) {
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return modificarAutor(id, modelo);
            
            // Same as above: alternativa que no termina de funcionar
            //return "autores/modificar/" + id;
        }

    }
    
    @GetMapping("/baja/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {
        
        try {
            autorService.darBaja(id);
        } catch (AutorServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "redirect:/autores/listado_autores";
    }
    
    @GetMapping("/alta/{id}")
    public String darAlta(@PathVariable String id, ModelMap modelo) {
        
        try {
            autorService.darAlta(id);
        } catch (AutorServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "redirect:/autores/listado_autores";
    }

}
