package libreria.spring.LibreriaSpring.controladores;

import libreria.spring.LibreriaSpring.servicios.AutorService;
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
@RequestMapping("/autores")
public class AutorController {
    
    @Autowired
    private AutorService autorService;

    @GetMapping("/")
    public String autores() {
        return "autores/autores.html";
    }
    
    @GetMapping("/nuevo_autor")
    public String nuevo_autor() {
        return "autores/nuevo_autor.html";
    }
    
    @PostMapping("/nuevo_autor")
    public String guardar_autor(ModelMap modelo, @RequestParam String nombre) {
        
        try {
            
            autorService.crearNuevoAutor(nombre);
            modelo.put("exito", "¡Autor guardado con éxito!");
            return "autores/nuevo_autor.html";
            
        } catch (Exception e) {
            
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return "autores/nuevo_autor.html";
        }
    }
    
    @GetMapping("/listado_autores")
    public String listado_autores() {
        return "autores/listado_autores.html";
    }
    
}
