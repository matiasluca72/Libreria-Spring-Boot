package libreria.spring.LibreriaSpring.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.spring.LibreriaSpring.entidades.Cliente;
import libreria.spring.LibreriaSpring.entidades.Prestamo;
import libreria.spring.LibreriaSpring.excepciones.ClienteServiceException;
import libreria.spring.LibreriaSpring.repositorios.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase Service de la Clase Cliente para realizar la lógica de negocio
 *
 * @author Matias Luca Soto
 */
@Service
public class ClienteService {

    //ATRIBUTOS REPOSITORIO
    @Autowired
    private ClienteRepositorio clienteRepositorio;

    /**
     * Método para crear una nueva instancia de la Clase Cliente con sus atributos seteados y persistido en la base de datos
     *
     * @param dni Atributo del Nuevo Objeto Cliente
     * @param nombre Atributo del Nuevo Objeto Cliente
     * @param apellido Atributo del Nuevo Objeto Cliente
     * @param telefono Atributo del Nuevo Objeto Cliente
     * @throws ClienteServiceException Si algún atributo no cumple con las verificaciones
     */
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

    /**
     * Método para actualizar los atributos de una instancia de la Clase Cliente ya existente y persistida en la base de datos
     *
     * @param id De la instancia de Cliente
     * @param dni Atributo actualizado del Objeto Cliente
     * @param nombre Atributo actualizado del Objeto Cliente
     * @param apellido Atributo actualizado del Objeto Cliente
     * @param telefono Atributo actualizado del Objeto Cliente
     * @throws ClienteServiceException Si algún atributo no cumple con las verificaciones o no se encuentra el Cliente a modificar
     */
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

    /**
     * Método para dar de baja un Cliente en la base de datos y persistir los cambios
     *
     * @param idCliente De la instancia de Cliente a dar de baja
     * @throws ClienteServiceException Si la instancia no pudo ser encontrada
     */
    @Transactional
    public void darBaja(String idCliente) throws ClienteServiceException {

        //Buscamos la instancia en la base de datos
        Optional<Cliente> respuesta = clienteRepositorio.findById(idCliente);

        //Si se encuentra, se setea la baja y se persisten los cambios
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            cliente.setAlta(false);
            clienteRepositorio.save(cliente);
        } else {
            throw new ClienteServiceException("No se ha encontrado el cliente solicitado.");
        }
    }

    /**
     * Método para dar de alta un Cliente en la base de datos y persistir los cambios
     *
     * @param idCliente De la instancia de Cliente a dar de alta
     * @throws ClienteServiceException Si la instancia no pudo ser encontrada
     */
    @Transactional
    public void darAlta(String idCliente) throws ClienteServiceException {

        //Buscamos la instancia en la base de datos
        Optional<Cliente> respuesta = clienteRepositorio.findById(idCliente);

        //Si se encuentra, se setea el alta y se persisten los cambios
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            cliente.setAlta(true);
            clienteRepositorio.save(cliente);
        } else {
            throw new ClienteServiceException("No se ha encontrado el cliente solicitado.");
        }
    }

    /**
     * Método de solo lectura para buscar un Cliente en la base de datos por su ID
     *
     * @param id Perteneciente al Cliente a buscar
     * @return El Cliente encontrado con ese ID
     * @throws ClienteServiceException Si no se encuentra ningún cliente
     */
    @Transactional(readOnly = true)
    public Cliente buscarPorId(String id) throws ClienteServiceException {

        //Se busca utilizando un método de la Clase Repository y se lo devuelve. Sino, se lanza la excepción
        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new ClienteServiceException("No se ha encontrado el cliente solicitado.");
        }
    }

    /**
     * Método de solo lectura para buscar un Cliente en la base de datos por su atributo 'DNI'
     *
     * @param dni Perteneciente al Cliente a buscar
     * @return El Cliente encontrado con ese atributo 'dni'
     * @throws ClienteServiceException Si no se encuentra ningún cliente
     */
    @Transactional(readOnly = true)
    public Cliente buscarPorDni(Long dni) throws ClienteServiceException {

        //Se busca utilizando un método de la Clase Repository y se lo devuelve. Sino, se lanza la excepción
        Cliente cliente = clienteRepositorio.buscarPorDni(dni);
        if (cliente != null) {
            return cliente;
        } else {
            throw new ClienteServiceException("No se ha encontrado el cliente solicitado.");
        }
    }

    /**
     * Método para listar a TODOS los clientes sin discriminación alguna
     *
     * @return Un List con todos los Clientes persistidos en la base de datos
     * @throws ClienteServiceException Si hubo algún problema con la base de datos
     */
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() throws ClienteServiceException {

        //Se utiliza un método de la Clase Repository y se lo devuelve. Sino, se lanza una excepción
        try {
            return clienteRepositorio.findAll();
        } catch (Exception e) {
            throw new ClienteServiceException("Hubo un problema para traer a los clientes. Por favor, reintente nuevamente.");
        }
    }

    /**
     * Método que lista a todos los Clientes activos en la base de datos
     *
     * @return Un List con todos los Clientes activos
     * @throws ClienteServiceException Si hubo algún problema conectando a la base de datos
     */
    public List<Cliente> listarActivos() throws ClienteServiceException {

        //Se traen a todos los Clientes y se guardan en un 2do List todos aquellos que estén activos y se lo devuelve
        try {
            List<Cliente> todos = listarTodos();
            List<Cliente> activos = new ArrayList();
            for (Cliente cliente : todos) {
                if (cliente.getAlta()) {
                    activos.add(cliente);
                }
            }
            return activos;
        } catch (ClienteServiceException e) {
            throw new ClienteServiceException("Hubo un problema para traer a los clientes. Por favor, reintente nuevamente.");
        }
    }

    /**
     * Lista todos los prestamos que estén activos de un Cliente en particular pasado como parámetro
     *
     * @param cliente Al que le pertenezcan los préstamos
     * @return Una lista con todos los préstamos activos de ese Cliente
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarPrestamosActivos(Cliente cliente) {

        //Se traen a todos los préstamos del Cliente pasado como argumento y se guardan en un 2do List solo aquellos que sigan activos
        List<Prestamo> prestamos = cliente.getPrestamos();
        List<Prestamo> prestamosActivos = new ArrayList();
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getAlta()) {
                prestamosActivos.add(prestamo);
            }
        }
        return prestamosActivos;
    }

    /**
     * Método de verificación de los atributos de un Cliente. Lanza una excepción si alguno no cumple con los requisitos.
     *
     * @param dni Atributo de la instancia Cliente
     * @param nombre Atributo de la instancia Cliente
     * @param apellido Atributo de la instancia Cliente
     * @param telefono Atributo de la instancia Cliente
     * @param nuevoCliente Boolean que indica si se trata de una nueva instancia o la modificación de una instancia ya existente
     * @throws ClienteServiceException Si algún atributo no es válido
     */
    @Transactional(readOnly = true)
    private void verificar(Long dni, String nombre, String apellido, String telefono, boolean nuevoCliente) throws ClienteServiceException {

        //El dni no puede estar repetido si se trata de un nuevo Cliente, tampoco puede estar null.
        if (clienteRepositorio.buscarPorDni(dni) != null && nuevoCliente) {
            throw new ClienteServiceException("El DNI ingresado ya pertenece a otro cliente.");
        } else if (dni == null) {
            throw new ClienteServiceException("El DNI no puede estar vacío.");
        }

        //El nombre no puede estar vacío o null
        if (nombre.isEmpty() || nombre == null) {
            throw new ClienteServiceException("El nombre no puede estar vacío.");
        }

        //El apellido no puede estar vacío o null
        if (apellido.isEmpty() || apellido == null) {
            throw new ClienteServiceException("El apellido no puede estar vacío.");
        }

        //El teléfono no puede estar vacío o null
        if (telefono.isEmpty() || telefono == null) {
            throw new ClienteServiceException("El telefono no puede estar vacío.");
        }
    }

    /**
     * Método para verificar los atributos de una actualización de un Cliente ya existente. Lanza una excepción si no se cumplen los requisitos.
     *
     * @param cliente Instancia a actualizar
     * @param dni Atributos actualizados a settear en el Objeto Cliente
     * @param nombre Atributos actualizados a settear en el Objeto Cliente
     * @param apellido Atributos actualizados a settear en el Objeto Cliente
     * @param telefono Atributos actualizados a settear en el Objeto Cliente
     * @throws ClienteServiceException Si no se pasa alguna verificación
     */
    private void verificarCambios(Cliente cliente, Long dni, String nombre, String apellido, String telefono) throws ClienteServiceException {

        //Tiene que existir mínimamente un cambio en al menos un atributo para efectuar la actualización
        if (cliente.getDni().equals(dni) && cliente.getNombre().equals(nombre) && cliente.getApellido().equals(apellido) && cliente.getTelefono().equals(telefono)) {
            throw new ClienteServiceException("No se ha registrado ningún cambio.");
        }

        //El atributo 'dni' no puede pertenecer a otra instancia de Cliente que no sea la de ese mismo cliente
        if (clienteRepositorio.buscarPorDni(dni) != null && !(cliente.getDni().equals(dni))) {
            throw new ClienteServiceException("El DNI ingresado pertenece a otro usuario.");
        }
    }
}
