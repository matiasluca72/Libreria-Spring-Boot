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
 * Controlador de la Clase Autores
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
     * Método que devuelve el formulario vacío para poder ingresar un nuevo Autor a la base de datos
     *
     * @return Formulario para ingresar un nuevo Autor
     */
    @GetMapping("/nuevo_autor")
    public String nuevo_autor() {
        return "autores/nuevo_autor";
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
            //Se crea y se persiste el nuevo Autor usando el método de la Clase Service
            autorService.crearNuevoAutor(nombre);
            modelo.put("exito", "¡Autor guardado con éxito!");

        } catch (AutorServiceException e) {

            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            modelo.put("nombre", nombre); //Inyección del argumento ingresado para no volver a teclearlo desde 0
        }
        return "autores/nuevo_autor";
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
        } catch (AutorServiceException e) {
            modelo.put("error", "Hubo un problema: " + e.getMessage());
        }

        return "autores/listado_autores";
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
            //Se inyecta en el modelo el Objeto Autor correspondiente al id recibido como parámetro
            modelo.put("autor", autorService.buscarPorId(id));
        } catch (AutorServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "autores/modificar_autor";
    }

    /**
     * Método POST del formulario de Modificar Autor. Si la actualización se efectua de forma exitosa, devuelve el Listado actualizado de Autores. Sino, devuelve nuevamente el formulario para actualizar el Autor
     *
     * @param id Del Libro que se modifica
     * @param modelo De la vista
     * @param nombre Atributo actualizado de la instancia
     * @return Si todo sale bien, el listado de autores
     */
    @PostMapping("/modificar/{id}") //El {id} es el que se recibe como parámetro con la anotación @PathVariable
    public String guardarModificacion(@PathVariable String id, ModelMap modelo, @RequestParam String nombre) {

        try {
            //Se modifica la instancia en la base de datos usando un método de la Clase Service
            autorService.modificarAutor(id, nombre);
            modelo.put("exito", "¡Autor modificado con éxito!");
            return listado_autores(modelo);

            /* Alternativa para devolver la vista, pero da errores de servidor
            List<Autor> autores = autorService.listarTodos();
            modelo.addAttribute("autores", autores);
            return "autores/listado_autores.html"; */
 /* Llega a la página correctamente pero no logra llevarse consigo el ModelMap,
            por lo cual nunca muestra el mensaje de éxito/error
            return "redirect:/autores/listado_autores"; */
 /* Método encontrado en Google, pero no funciona (o al menos no está bien implementado)
            attr.addAttribute("exito", "¡Autor modificado con éxito!"); */
        } catch (AutorServiceException e) {
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return modificarAutor(id, modelo);

            /* Alternativa para devolver la vista, pero da errores de servidor
            return "autores/modificar/" + id; */
 /* Llega a la página correctamente pero no logra llevarse consigo el ModelMap,
            por lo cual nunca muestra el mensaje de éxito/error
            return "redirect:/autores/modificar/" + id; */
 /* Método encontrado en Google, pero no funciona (o al menos no está bien implementado)
            attr.addAttribute("error", "¡Algo salió mal! " + e.getMessage()); */
        }
    }

    /**
     * Devuelve la vista del Listado de Autores una vez dado de baja la instancia seleccionada
     *
     * @param id Del Autor a dar de baja
     * @param modelo De la vista
     * @return redireccionamiento al listado de autores
     */
    @GetMapping("/baja/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {

        try {
            //Se da de baja usando un método de la Clase Service
            autorService.darBaja(id);
            return "redirect:/autores/listado_autores";
        } catch (AutorServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
            return listado_autores(modelo);
        }
    }

    /**
     * Devuelve la vista del Listado de Autores una vez dado de alta la instancia seleccionada
     *
     * @param id Del Autor a dar de alta
     * @param modelo De la vista
     * @return redireccionamiento al listado de autores
     */
    @GetMapping("/alta/{id}")
    public String darAlta(@PathVariable String id, ModelMap modelo) {

        try {
            //Se da de alta usando un método de la Clase Service
            autorService.darAlta(id);
            return "redirect:/autores/listado_autores";
        } catch (AutorServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
            return listado_autores(modelo);
        }
    }

}
