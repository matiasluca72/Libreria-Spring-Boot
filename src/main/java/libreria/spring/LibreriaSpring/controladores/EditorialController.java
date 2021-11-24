package libreria.spring.LibreriaSpring.controladores;

import java.util.List;
import libreria.spring.LibreriaSpring.entidades.Editorial;
import libreria.spring.LibreriaSpring.excepciones.EditorialServiceException;
import libreria.spring.LibreriaSpring.servicios.EditorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de la Entidad Editorial
 *
 * @author Matias Luca Soto
 */
@Controller
@RequestMapping("/editoriales")
public class EditorialController {

    //ATRIBUTOS - SERVICES
    @Autowired
    private EditorialService editorialService;

    /**
     * Controlador que devuelve la vista con el formulario para ingresar una nueva editorial
     *
     * @return vista con el formulario para ingresar una nueva editorial
     */
    @GetMapping("/nueva_editorial")
    public String nueva_editorial() {
        return "editoriales/nueva_editorial";
    }

    /**
     * Controlador POST del formulario para recibir el argumento ingresado y persistir los cambios
     *
     * @param modelo de la vista
     * @param nombre Atributo actualizado de la instancia
     * @return El formulario de nueva Editorial con un mensaje de éxito o de error
     */
    @PostMapping("/nueva_editorial")
    public String guardar_editorial(ModelMap modelo, @RequestParam String nombre) {

        try {
            //Uso de un método de la Clase Service
            editorialService.crearNuevaEditorial(nombre);
            modelo.put("exito", "¡Editorial guardada con éxito!");

        } catch (EditorialServiceException e) {
            //Inyección del mensaje de error y del último valor ingresado para mejorar UX
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            modelo.put("nombre", nombre);
        }
        return "editoriales/nueva_editorial";
    }

    /**
     * Controlador del listado de editoriales
     *
     * @param modelo De la vista
     * @return Listado con todas las editoriales
     */
    @GetMapping("/listado_editoriales")
    public String listado_editoriales(ModelMap modelo) {

        try {
            //Traemos a todas las editoriales usando un método de la Clase Service y lo inyectamos en el ModelMap
            List<Editorial> editoriales = editorialService.listarTodos();
            modelo.addAttribute("editoriales", editoriales);

        } catch (EditorialServiceException e) {
            modelo.put("error", "Hubo un problema: " + e.getMessage());
        }

        return "editoriales/listado_editoriales";
    }

    /**
     * Controlador que devuelve el formulario de Modificar Editorial con los atributos actuales prellenados
     *
     * @param id De la instancia a modificar
     * @param modelo De la vista
     * @return el formulario de Modificar Editorial con los atributos actuales prellenados
     */
    @GetMapping("/modificar/{id}")
    public String modificarEditorial(@PathVariable String id, ModelMap modelo) {

        try {
            //Inyectamos los datos de la instancia usando un método de la Clase Service para traerlo
            modelo.put("editorial", editorialService.buscarPorId(id));
        } catch (EditorialServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "editoriales/modificar_editorial";
    }

    /**
     * Controlador POST para recibir los argumentos ingresados e intentar persistir las modificaciones
     *
     * @param id De la instancia a modificar
     * @param modelo De la vista
     * @param nombre Atributo actualizado
     * @return Si todo sale bien, el listado de editoriales. Si algo falla, el formulario de Modificar Editorial nuevamente
     */
    @PostMapping("/modificar/{id}")
    public String guardarModificacion(@PathVariable String id, ModelMap modelo, @RequestParam String nombre) {

        try {
            //Realizamos los cambios con el método de la Clase Service y devolvemos el listado si todo sale bien
            editorialService.modificarEditorial(id, nombre);
            modelo.put("exito", "¡Editorial modificada con éxito!");
            return listado_editoriales(modelo);
        } catch (EditorialServiceException e) {
            //Si saltó alguna excepción, se inyecta el mensaje de error y se regresa al formulario de Modificar Editorial
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return modificarEditorial(id, modelo);
        }
    }

    /**
     * Controlador para dar de baja una instancia específica
     *
     * @param id De la instancia a modificar
     * @param modelo De la vista
     * @return Listado actualizado de Editoriales
     */
    @GetMapping("/baja/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {

        try {
            editorialService.darBaja(id);
            return "redirect:/editoriales/listado_editoriales";
        } catch (EditorialServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
            return listado_editoriales(modelo);
        }
    }

    /**
     * Controlador para dar de alta una instancia específica
     *
     * @param id De la instancia a modificar
     * @param modelo De la vista
     * @return Listado actualizado de Editoriales
     */
    @GetMapping("/alta/{id}")
    public String darAlta(@PathVariable String id, ModelMap modelo) {

        try {
            editorialService.darAlta(id);
            return "redirect:/editoriales/listado_editoriales";
        } catch (EditorialServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
            return listado_editoriales(modelo);
        }
    }
}
