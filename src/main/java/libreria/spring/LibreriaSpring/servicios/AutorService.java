package libreria.spring.LibreriaSpring.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.spring.LibreriaSpring.entidades.Autor;
import libreria.spring.LibreriaSpring.excepciones.AutorServiceException;
import libreria.spring.LibreriaSpring.repositorios.AutorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase Service de la Clase Autor para realizar la lógica de negocio
 *
 * @author Matias Luca Soto
 */
@Service
public class AutorService {

    //ATIBUTOS - REPOSITORIOS
    @Autowired
    private AutorRepositorio autorRepositorio;

    /**
     * Método para crear un Nuevo Objeto Autor y persistirlo en la base de datos
     *
     * @param nombre Atributo del nuevo Autor a crear
     * @throws AutorServiceException Si el nombre está vacío, null o ya se ha persistido otro Autor con este nombre
     */
    @Transactional
    public void crearNuevoAutor(String nombre) throws AutorServiceException {

        //Eliminación de espacios innecesarios
        nombre = nombre.trim();

        //Verificación de que el nombre sea válido para su persistencia
        verificar(nombre);

        //Se busca un Autor según el argumento recibido. Si retorna null, el nombre está disponible
        Autor autor = autorRepositorio.buscarPorNombre(nombre);
        if (autor == null) {
            autor = new Autor();
            autor.setNombre(nombre);
            autor.setAlta(true);
            autorRepositorio.save(autor); //Persistencia en la base de datos
        } else {
            throw new AutorServiceException("El autor que intenta crear ya existe.");
        }
    }

    /**
     * Método para modificar el principal atributo 'nombre' de un Autor ya persistido en la base de datos
     *
     * @param idAutor Del Autor a modificar
     * @param nombre Nuevo atributo del Objeto Autor
     * @throws AutorServiceException En caso que el idAutor no traiga ningún autor en la base de datos o el nuevo 'nombre' sea inválido
     */
    @Transactional
    public void modificarAutor(String idAutor, String nombre) throws AutorServiceException {

        //Eliminación de espacios innecesarios
        nombre = nombre.trim();

        //Verificación de que sea un nombre válido
        verificar(nombre);

        //Se busca el Autor según su ID en la base de datos
        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();

            //Verificamos que el nombre nuevo sea diferente al actual y que este no coincida con otro ya existente
            verificarCambios(autor, nombre);

            //Si se pasaron todas las verificaciones, seteamos los nuevos atributos y persistimos los cambios
            autor.setNombre(nombre);
            autor.setAlta(true);
            autorRepositorio.save(autor);
        } else {
            throw new AutorServiceException("No se ha encontrado el autor solicitado.");
        }
    }

    /**
     * Método para dar de baja un Autor en la base de datos y sacarlo de los listados para seleccionarlo en el formulario de Libros
     *
     * @param idAutor Del Autor a dar de baja
     * @throws AutorServiceException Si no se ha encontrado el Autor en cuestión
     */
    @Transactional
    public void darBaja(String idAutor) throws AutorServiceException {

        //Buscamos el Autor por su ID en la base de datos. Si se lo encuentra, se lo dá de baja y se persisten los cambios
        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autor.setAlta(false);
            autorRepositorio.save(autor);
        } else {
            throw new AutorServiceException("No se ha encontrado el autor solicitado.");
        }
    }

    /**
     * Método para dar de alta un Autor en la base de datos y reingresarlo en los listados para seleccionarlo en el formulario de Libros
     *
     * @param idAutor Del Autor a dar de alta
     * @throws AutorServiceException Si no se ha encontrado el Autor en cuestión
     */
    @Transactional
    public void darAlta(String idAutor) throws AutorServiceException {

        //Buscamos el Autor por su ID en la base de datos. Si se lo encuentra, se lo dá de alta y se persisten los cambios
        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autor.setAlta(true);
            autorRepositorio.save(autor);
        } else {
            throw new AutorServiceException("No se ha encontrado el autor solicitado.");
        }
    }

    /**
     * Devuelve el Autor con el id pasado como parámetro
     *
     * @param id del Autor a buscar
     * @return El autor con el id pasado como parámetro
     * @throws AutorServiceException Si no se encuentra el Autor
     */
    @Transactional(readOnly = true)
    public Autor buscarPorId(String id) throws AutorServiceException {

        //Buscamos el Autor en la base de datos usando el método de la Clase Repository
        Optional<Autor> respuesta = autorRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new AutorServiceException("No se ha encontrado el autor solicitado.");
        }
    }

    /**
     * Devuelve un listado con TODOS los autores, sin discriminación.
     *
     * @return Un List con todos los Autores de la base de datos
     * @throws AutorServiceException Si no se logra acceder a la base de datos
     */
    @Transactional(readOnly = true)
    public List<Autor> listarTodos() throws AutorServiceException {
        try {
            return autorRepositorio.findAll();
        } catch (Exception e) {
            throw new AutorServiceException("Hubo un problema para traer todos los autores. Reintente nuevamente.");
        }
    }

    /**
     * Devuelve un listado con todos los Autores dados de alta. No se listarán a los dados de baja.
     *
     * @return Un List de Autores dados de alta
     * @throws AutorServiceException Si no se logra acceder a la base de datos
     */
    public List<Autor> listarActivos() throws AutorServiceException {

        //Se traen a todos los autores y se guardan en una 2da List todos los autores que estén activos
        try {
            List<Autor> todos = listarTodos();
            List<Autor> activos = new ArrayList();
            for (Autor autor : todos) {
                if (autor.getAlta()) {
                    activos.add(autor);
                }
            }
            return activos;
        } catch (AutorServiceException e) {
            throw new AutorServiceException("Hubo un problema para traer a todos los autores. Reintente nuevamente.");
        }
    }

    /**
     * Método de verificación del atributo 'nombre'. Lanza una excepción si el nombre está vacío o null
     *
     * @param nombre Del Autor a verificar
     * @throws AutorServiceException Si el argumento está vacío o null
     */
    private void verificar(String nombre) throws AutorServiceException {
        if (nombre.isEmpty() || nombre == null) {
            throw new AutorServiceException("El nombre del Autor no puede estar vacio.");
        }
    }

    /**
     * Método para verificar que los cambios de una instancia ya persistida de Autor sean válidos
     * @param autor Instancia a modificar
     * @param nombre Nuevo atributo a settear
     * @throws AutorServiceException Si no se cumplen con los requisitos 
     */
    private void verificarCambios(Autor autor, String nombre) throws AutorServiceException {

        //El nuevo nombre debe ser diferente al ya seteado en la instancia y no debe pertenecer a otra instancia ajena a la que se va a modificar
        if (autor.getNombre().equals(nombre)) {
            throw new AutorServiceException("Debe ingresar un nombre diferente al actual.");
        } else if (autorRepositorio.buscarPorNombre(nombre) != null && !(autor.getNombre().equalsIgnoreCase(nombre))) {
            throw new AutorServiceException("El nuevo nombre de autor que ingresó ya existe.");
        }
    }

}
