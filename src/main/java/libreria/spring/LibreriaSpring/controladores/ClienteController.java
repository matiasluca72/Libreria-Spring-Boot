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
 * Controlador de la Clase Cliente
 *
 * @author Matias Luca Soto
 */
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    //ATRIBUTOS - Clase Service
    @Autowired
    private ClienteService clienteService;

    /**
     * Devuelve la vista con el formulario para ingresar un nuevo Cliente
     *
     * @return
     */
    @GetMapping("/nuevo_cliente")
    public String nuevo_cliente() {
        return "clientes/nuevo_cliente";
    }

    /**
     * Método POST para recibir los argumentos recibidos e intentar settear y persistir el nuevo Cliente en la base de datos
     *
     * @param modelo De la vista del formulario
     * @param dni Atributos del nuevo Cliente
     * @param nombre Atributos del nuevo Cliente
     * @param apellido Atributos del nuevo Cliente
     * @param telefono Atributos del nuevo Cliente
     * @return
     */
    @PostMapping("/nuevo_cliente")
    public String guardar_cliente(ModelMap modelo, @RequestParam Long dni, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String telefono) {

        try {
            //Se crea el Cliente utilizando un método de la Clase Service
            clienteService.crearCliente(dni, nombre, apellido, telefono);
            modelo.put("exito", "¡Cliente creado con éxito!");

        } catch (ClienteServiceException e) {

            //Inyección del mensaje de error junto a los argumentos previamente ingresados para mejorar la UX
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            modelo.put("dni", dni);
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("telefono", telefono);
        }
        return "clientes/nuevo_cliente";
    }

    /**
     * Controlador que devuelve la vista con todo el listado de Clientes guardados en la base de datos
     *
     * @param modelo de la vista
     * @return La vista con todo el Listado de Clientes
     */
    @GetMapping("/listado_clientes")
    public String listado_clientes(ModelMap modelo) {

        try {
            //Llamado de un métood de ClienteService para obtener el listado con todos los Clientes
            List<Cliente> clientes = clienteService.listarTodos();
            modelo.addAttribute("clientes", clientes);

        } catch (ClienteServiceException e) {
            modelo.put("error", "Hubo un problema: " + e.getMessage());
        }
        return "clientes/listado_clientes";
    }

    /**
     * Controlador del formulario para editar los atributos de una instancia de Cliente
     *
     * @param id De la instancia a modificar
     * @param modelo De la vista
     * @return El formulario de Modificar Cliente con los campos ya completados con los atributos actuales de la instancia
     */
    @GetMapping("modificar/{id}")
    public String modificarCliente(@PathVariable String id, ModelMap modelo) {

        try {
            //Inyección de los datos actuales de la instancia en el formulario de modificación
            modelo.put("cliente", clienteService.buscarPorId(id));
        } catch (ClienteServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
        }
        return "clientes/modificar_cliente";
    }

    /**
     * Controlador POST para recibir los argumentos ingresados e intentar persistir los cambios de la entidad en la base de datos
     *
     * @param id De la entidad modificada
     * @param modelo De la vista a imprimir
     * @param dni Atributos actualizados de la entidad
     * @param nombre Atributos actualizados de la entidad
     * @param apellido Atributos actualizados de la entidad
     * @param telefono Atributos actualizados de la entidad
     * @return El Listado de Clientes actualizados si todo salió bien. Sino, el formulario de modificación nuevamente con el mensaje de error
     */
    @PostMapping("modificar/{id}")
    public String guardarModificacion(@PathVariable String id, ModelMap modelo, @RequestParam Long dni, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String telefono) {

        try {
            //Llamada al método de Cliente Service para guardar los cambios
            clienteService.modificarCliente(id, dni, nombre, apellido, telefono);
            modelo.put("exito", "¡Cliente modificado con éxito!");

            //Si todo salió bien, devolvemos el listado de Clientes
            return listado_clientes(modelo);
        } catch (ClienteServiceException e) {

            //Si algo salió mal, se vuelve al mismo método
            modelo.put("error", "¡Algo salió mal! " + e.getMessage());
            return modificarCliente(id, modelo);
        }
    }

    /**
     * Método GET para dar de baja una entidad pasando su id como argumento y devolviendo el listado actualizado
     *
     * @param id De la entidad a dar de baja
     * @param modelo De la vista
     * @return El listado de Clientes actualizado
     */
    @GetMapping("/baja/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {

        try {
            clienteService.darBaja(id);
            return "redirect:/clientes/listado_clientes";
        } catch (ClienteServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
            return listado_clientes(modelo);
        }
    }

    /**
     * Método GET para dar de alta una entidad pasando su id como argumento y devolviendo el listado actualizado
     *
     * @param id De la entidad a dar de alta
     * @param modelo De la vista
     * @return El listado de Clientes actualizado
     */
    @GetMapping("/alta/{id}")
    public String darAlta(@PathVariable String id, ModelMap modelo) {

        try {
            clienteService.darAlta(id);
            return "redirect:/clientes/listado_clientes";
        } catch (ClienteServiceException e) {
            modelo.put("error", "Algo salió mal: " + e.getMessage());
            return listado_clientes(modelo);
        }
    }

}
