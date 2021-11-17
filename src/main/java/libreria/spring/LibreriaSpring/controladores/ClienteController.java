package libreria.spring.LibreriaSpring.controladores;

import java.util.List;
import libreria.spring.LibreriaSpring.entidades.Cliente;
import libreria.spring.LibreriaSpring.excepciones.ClienteServiceException;
import libreria.spring.LibreriaSpring.servicios.ClienteService;
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
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/")
    public String clientes() {
        return "clientes/clientes";
    }

    @GetMapping("/nuevo_cliente")
    public String nuevo_cliente() {
        return "clientes/nuevo_cliente";
    }

    @PostMapping("/nuevo_cliente")
    public String guardar_cliente(ModelMap modelo, @RequestParam Long dni, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String telefono) {

        try {

            clienteService.crearCliente(dni, nombre, apellido, telefono);
            modelo.put("exito", "¡Cliente creado con éxito!");

        } catch (ClienteServiceException e) {

            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            modelo.put("dni", dni);
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("telefono", telefono);
        }
        return "clientes/nuevo_cliente";
    }
    
    @GetMapping("/listado_clientes")
    public String listado_clientes(ModelMap modelo) {
        
        try {
            
            List<Cliente> clientes = clienteService.listarTodos();
            modelo.addAttribute("clientes", clientes);
            
        } catch (ClienteServiceException e) {
            modelo.put("error", "Hubo un problema: " + e.getMessage());
        }
        return "clientes/listado_clientes";
    }
    
    @GetMapping("modificar/{id}")
    public String modificarCliente(@PathVariable String id, ModelMap modelo) {
        
        try {
            modelo.put("cliente", clienteService.buscarPorId(id));
        } catch (ClienteServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "clientes/modificar_cliente";
    } 
    
    @PostMapping("modificar/{id}")
    public String guardarModificacion(@PathVariable String id, ModelMap modelo, @RequestParam Long dni, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String telefono) {
        
        try {
            clienteService.modificarCliente(id, dni, nombre, apellido, telefono);
            modelo.put("exito", "¡Cliente modificado con éxito!");
            return listado_clientes(modelo);
        } catch (ClienteServiceException e) {
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return modificarCliente(id, modelo);
        }
    }

    @GetMapping("/baja/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {
        
        try {
            clienteService.darBaja(id);
        } catch (ClienteServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "redirect:/clientes/listado_clientes";
    }
    
    @GetMapping("/alta/{id}")
    public String darAlta(@PathVariable String id, ModelMap modelo) {
        
        try {
            clienteService.darAlta(id);
        } catch (ClienteServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "redirect:/clientes/listado_clientes";
    }
    
    
}
