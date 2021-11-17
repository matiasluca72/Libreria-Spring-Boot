package libreria.spring.LibreriaSpring.servicios;

import java.util.List;
import java.util.Optional;
import libreria.spring.LibreriaSpring.entidades.Cliente;
import libreria.spring.LibreriaSpring.excepciones.ClienteServiceException;
import libreria.spring.LibreriaSpring.repositorios.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Matias Luca Soto
 */
@Service
public class ClienteService {

    //ATRIBUTOS REPOSITORIO
    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Transactional
    public void crearCliente(Long dni, String nombre, String apellido, String telefono) throws ClienteServiceException {

        //Removemos espacios innecesarios
        nombre = nombre.trim();
        apellido = apellido.trim();
        telefono = telefono.trim();

        //validamos los parámetros
        verificar(dni, nombre, apellido, telefono, true);

        //Creación del Objeto Cliente
        Cliente cliente = new Cliente();

        //Seteamos los atributos ya validados
        cliente.setDni(dni);
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setTelefono(telefono);
        cliente.setAlta(true);

        //Persistimos el Cliente en la base de datos
        clienteRepositorio.save(cliente);
    }

    @Transactional
    public void modificarCliente(String id, Long dni, String nombre, String apellido, String telefono) throws ClienteServiceException {

        //Removemos espacios innecesarios
        nombre = nombre.trim();
        apellido = apellido.trim();
        telefono = telefono.trim();

        //validamos los parámetros
        verificar(dni, nombre, apellido, telefono, false);

        //Buscamos el Cliente a modificar
        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Cliente cliente = respuesta.get();

            //Verificamos si se ha realizado al menos un cambio
            verificarCambios(cliente, dni, nombre, apellido, telefono);

            //Seteamos los nuevos atributos
            cliente.setDni(dni);
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setTelefono(telefono);
            cliente.setAlta(true);

            //Persistimos el cliente modificado
            clienteRepositorio.save(cliente);

        } else {
            throw new ClienteServiceException("No se ha encontrado el cliente solicitado.");
        }
    }
    
    @Transactional
    public void darBaja(String idCliente) throws ClienteServiceException {
        
        Optional<Cliente> respuesta = clienteRepositorio.findById(idCliente);
        
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            cliente.setAlta(false);
            clienteRepositorio.save(cliente);
        } else {
            throw new ClienteServiceException("No se ha encontrado el cliente solicitado.");
        }
    }
    
    @Transactional
    public void darAlta(String idCliente) throws ClienteServiceException {
        
        Optional<Cliente> respuesta = clienteRepositorio.findById(idCliente);
        
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            cliente.setAlta(true);
            clienteRepositorio.save(cliente);
        } else {
            throw new ClienteServiceException("No se ha encontrado el cliente solicitado.");
        }
    }
    
    @Transactional(readOnly = true)
    public Cliente buscarPorId(String id) throws ClienteServiceException {
        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new ClienteServiceException("No se ha encontrado el cliente solicitado.");
        }
    }
    @Transactional(readOnly = true)
    public Cliente buscarPorDni(Long dni) throws ClienteServiceException {
        Cliente cliente = clienteRepositorio.buscarPorDni(dni);
        if (cliente != null) {
            return cliente;
        } else {
            throw new ClienteServiceException("No se ha encontrado el cliente solicitado.");
        }
    }
    
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() throws ClienteServiceException {
        try {
            return clienteRepositorio.findAll();
        } catch (Exception e) {
            throw new ClienteServiceException("Hubo un problema para traer a los clientes.");
        }
    }

    @Transactional(readOnly = true)
    private void verificar(Long dni, String nombre, String apellido, String telefono, boolean nuevoCliente) throws ClienteServiceException {
        if (clienteRepositorio.buscarPorDni(dni) != null && nuevoCliente) {
            throw new ClienteServiceException("El DNI ingresado ya pertenece a otro cliente.");
        } else if (dni == null) {
            throw new ClienteServiceException("El DNI no puede estar vacío.");
        }
        if (nombre.isEmpty() || nombre == null) {
            throw new ClienteServiceException("El nombre no puede estar vacío.");
        }
        if (apellido.isEmpty() || apellido == null) {
            throw new ClienteServiceException("El apellido no puede estar vacío.");
        }
        if (telefono.isEmpty() || telefono == null) {
            throw new ClienteServiceException("El telefono no puede estar vacío.");
        }
    }

    private void verificarCambios(Cliente cliente, Long dni, String nombre, String apellido, String telefono) throws ClienteServiceException {
        if (cliente.getDni().equals(dni) && cliente.getNombre().equals(nombre) && cliente.getApellido().equals(apellido) && cliente.getTelefono().equals(telefono)) {
            throw new ClienteServiceException("No se ha registrado ningún cambio.");
        }
    }
}
