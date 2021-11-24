package libreria.spring.LibreriaSpring.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.spring.LibreriaSpring.entidades.Editorial;
import libreria.spring.LibreriaSpring.excepciones.EditorialServiceException;
import libreria.spring.LibreriaSpring.repositorios.EditorialRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase Service de la Clase Editorial para realizar la lógica de negocio
 *
 * @author Matias Luca Soto
 */
@Service
public class EditorialService {

    //ATRIBTOS - REPOSITORIOS
    @Autowired
    private EditorialRepositorio editorialRepositorio;

    /**
     * Método para crear un Nuevo Objeto Editorial y persistirlo en la base de datos
     *
     * @param nombre Atributo de la nueva instancia de Editorial a crear y persistir
     * @throws EditorialServiceException Si el nombre está vacío, null o pertenece a otra instancia ya persistida
     */
    @Transactional
    public void crearNuevaEditorial(String nombre) throws EditorialServiceException {

        //Eliminación de espacios innecesarios
        nombre = nombre.trim();

        //verificación del atributo
        verificar(nombre);

        //Si el atributo es válido y no pertenece a otra instancia, se lo setea, se da el alta y se lo persiste en la base de datos
        Editorial editorial = editorialRepositorio.buscarPorNombre(nombre);
        if (editorial == null) {
            editorial = new Editorial();
            editorial.setNombre(nombre);
            editorial.setAlta(true);
            editorialRepositorio.save(editorial);
        } else {
            throw new EditorialServiceException("La editorial que intenta crear ya existe.");
        }
    }

    /**
     * Método para modificar el atributo 'nombre' de una instancia de Editorial ya persistida en la base de datos
     *
     * @param idEditorial De la instancia a modificar
     * @param nombre Nuevo atributo del Objeto Editorial
     * @throws EditorialServiceException En caso de que el idEditorial no traiga ningún resultado o el atributo 'nombre' no sea válido
     */
    @Transactional
    public void modificarEditorial(String idEditorial, String nombre) throws EditorialServiceException {

        //Eliminación de espacios innecesarios
        nombre = nombre.trim();

        //Verificación del nuevo atributo
        verificar(nombre);

        //Buscamos la Editorial en la base de datos con su ID. Si se lo encuentra, se verifica y se setea el nuevo atributo
        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();

            //Verificamos que el nombre nuevo sea diferente al actual y que este no coincida con otro ya existente
            verificarCambios(editorial, nombre);

            //Seteamos y persistimos
            editorial.setNombre(nombre);
            editorial.setAlta(true);
            editorialRepositorio.save(editorial);
        } else {
            throw new EditorialServiceException("No se ha encontrado la editorial solicitada.");
        }
    }

    /**
     * Método para dar de baja el boolean de una instancia de la Clase Editorial y así deshabilitarlo de los listados en los formularios de Libros
     *
     * @param idEditorial De la instancia a dar de baja
     * @throws EditorialServiceException Si no se encuentra la instancia solicitada
     */
    @Transactional
    public void darBaja(String idEditorial) throws EditorialServiceException {

        //Buscamos a la instancia y si la encuentra, seteamos la baja y persistimos
        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setAlta(false);
            editorialRepositorio.save(editorial);
        } else {
            throw new EditorialServiceException("No se ha encontrado la editorial solicitada.");
        }
    }

    /**
     * Método para dar de alta el boolean de una instancia de la Clase Editorial y así rehabilitarlo de los listados en los formularios de Libros
     *
     * @param idEditorial De la instancia a dar de alta
     * @throws EditorialServiceException Si no se encuentra la instancia solicitada
     */
    @Transactional
    public void darAlta(String idEditorial) throws EditorialServiceException {

        //Buscamos a la instancia y si la encuentra, seteamos el alta y persistimos
        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setAlta(true);
            editorialRepositorio.save(editorial);
        } else {
            throw new EditorialServiceException("No se ha encontrado la editorial solicitada.");
        }
    }

    /**
     * Devuelve una Editorial de la base de datos que pertenezca al String id pasado como parámetro
     *
     * @param id De la entidad Editorial a buscar
     * @return La instancia correspondiente al id
     * @throws EditorialServiceException Si no se encuentra la instancia solicitada
     */
    @Transactional(readOnly = true)
    public Editorial buscarPorId(String id) throws EditorialServiceException {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new EditorialServiceException("No se ha encontrado la editorial solicitada.");
        }
    }

    /**
     * Devuelve una List con todas las instancias de Editorial en la base de datos, sin discriminar ninguna instancia
     *
     * @return List con todas las Editoriales guardadas en la base de datos
     * @throws EditorialServiceException Si hubo algún problema con la base de datos
     */
    @Transactional(readOnly = true)
    public List<Editorial> listarTodos() throws EditorialServiceException {
        try {
            return editorialRepositorio.findAll();
        } catch (Exception e) {
            throw new EditorialServiceException("Hubo un problema para traer todas las editoriales. Por favor, reintente nuevamente");
        }
    }

    /**
     * Devuelve una List con todas las instancias activas de la tabla Editorial en la base de datos
     *
     * @return Una Lista con todas las Editoriales activas
     * @throws EditorialServiceException Si no se pudo traer la lista de la base de datos
     */
    public List<Editorial> listarActivos() throws EditorialServiceException {

        //Traigo la lista con todas las editoriales y guardo en una 2da lista solo las Editoriales que tengan el Alta = true
        try {
            List<Editorial> todos = listarTodos();
            List<Editorial> activos = new ArrayList();
            for (Editorial editorial : todos) {
                if (editorial.getAlta()) {
                    activos.add(editorial);
                }
            }
            return activos;
        } catch (EditorialServiceException e) {
            throw new EditorialServiceException("Hubo un problema para traer a todos los autores. Reintente nuevamente.");
        }
    }

    /**
     * Verifica que el atributo 'nombre' sea válido para la entidad a settear
     *
     * @param nombre Atributo que va a ser setteado en una instancia de Editorial
     * @throws EditorialServiceException Si el parámetro no cumple los requisitos
     */
    private void verificar(String nombre) throws EditorialServiceException {
        
        //No puede estar vacío ni estar null
        if (nombre.isEmpty() || nombre == null) {
            throw new EditorialServiceException("El nombre de la editorial no puede estar vacío.");
        }
    }

    /**
     * Verifica que el parámetro nombre sea válido para ser setteado en el parámetro Editorial
     * @param editorial A ser setteado con el atributo
     * @param nombre El atributo a settear
     * @throws EditorialServiceException Si no cumple los requisitos
     */
    private void verificarCambios(Editorial editorial, String nombre) throws EditorialServiceException {

        //El nuevo nombre debe ser diferente al ya seteado en la instancia y no debe pertenecer a otra instancia ajena a la que se va a modificar
        if (editorial.getNombre().equals(nombre)) {
            throw new EditorialServiceException("Debe ingresar un nombre diferente al actual.");
        } else if (editorialRepositorio.buscarPorNombre(nombre) != null && !(editorial.getNombre().equalsIgnoreCase(nombre))) {
            throw new EditorialServiceException("El nombre de editorial que intenta modificar ya existe.");
        }

    }

}
