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
 *
 * @author Matias Luca Soto
 */
@Controller
@RequestMapping("/editoriales")
public class EditorialController {

    @Autowired
    private EditorialService editorialService;

    @GetMapping("/")
    public String editoriales() {
        return "editoriales/editoriales";
    }

    @GetMapping("/nueva_editorial")
    public String nueva_editorial() {
        return "editoriales/nueva_editorial";
    }

    @PostMapping("/nueva_editorial")
    public String guardar_editorial(ModelMap modelo, @RequestParam String nombre) {

        try {

            editorialService.crearNuevaEditorial(nombre);
            modelo.put("exito", "¡Editorial guardada con éxito!");

        } catch (EditorialServiceException e) {

            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            modelo.put("nombre", nombre);
        }
        return "editoriales/nueva_editorial";
    }

    @GetMapping("/listado_editoriales")
    public String listado_editoriales(ModelMap modelo) {

        try {

            List<Editorial> editoriales = editorialService.listarTodos();
            modelo.addAttribute("editoriales", editoriales);

        } catch (EditorialServiceException e) {
            modelo.put("error", "Hubo un problema: " + e.getMessage());
        }

        return "editoriales/listado_editoriales";
    }

    @GetMapping("/modificar/{id}")
    public String modificarEditorial(@PathVariable String id, ModelMap modelo) {

        try {
            modelo.put("editorial", editorialService.buscarPorId(id));
        } catch (EditorialServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "editoriales/modificar_editorial";
    }

    @PostMapping("/modificar/{id}")
    public String guardarModificacion(@PathVariable String id, ModelMap modelo, @RequestParam String nombre) {

        try {
            editorialService.modificarEditorial(id, nombre);
            modelo.put("exito", "¡Editorial modificada con éxito!");
            return listado_editoriales(modelo);
        } catch (EditorialServiceException e) {
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return modificarEditorial(id, modelo);
        }
    }

    @GetMapping("/baja/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {

        try {
            editorialService.darBaja(id);
        } catch (EditorialServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "redirect:/editoriales/listado_editoriales";
    }

    @GetMapping("/alta/{id}")
    public String darAlta(@PathVariable String id, ModelMap modelo) {

        try {
            editorialService.darAlta(id);
        } catch (EditorialServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "redirect:/editoriales/listado_editoriales";
    }
}
