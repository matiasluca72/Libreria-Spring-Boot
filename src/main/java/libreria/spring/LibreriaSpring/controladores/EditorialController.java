package libreria.spring.LibreriaSpring.controladores;

import libreria.spring.LibreriaSpring.servicios.EditorialService;
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
@RequestMapping("/editoriales")
public class EditorialController {

    @Autowired
    private EditorialService editorialService;
    
    @GetMapping("/")
    public String editoriales() {
        return "editoriales/editoriales.html";
    }

    @GetMapping("/nueva_editorial")
    public String nueva_editorial() {
        return "editoriales/nueva_editorial.html";
    }

    @PostMapping("/nueva_editorial")
    public String guardar_editorial(ModelMap modelo, @RequestParam String nombre) {

        try {
            
            editorialService.crearNuevaEditorial(nombre);
            modelo.put("exito", "¡Editorial guardada con éxito!");
            return "editoriales/nueva_editorial.html";
            
        } catch (Exception e) {

            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return "editoriales/nueva_editorial.html";
        }
    }

    @GetMapping("/listado_editoriales")
    public String listado_editoriales() {
        return "editoriales/listado_editoriales.html";
    }

}
