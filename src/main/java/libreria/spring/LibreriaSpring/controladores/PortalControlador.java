package libreria.spring.LibreriaSpring.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador del index del proyecto
 *
 * @author Matias Luca Soto
 */
@Controller
@RequestMapping("/")
public class PortalControlador {

    /**
     * Controlador del index del proyecto
     *
     * @return Vista del Home del proyecto
     */
    @GetMapping("/")
    public String index() {
        return "index.html";
    }

}
