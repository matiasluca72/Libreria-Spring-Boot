package libreria.spring.LibreriaSpring.servicios;

import java.util.List;
import java.util.Optional;
import libreria.spring.LibreriaSpring.entidades.Editorial;
import libreria.spring.LibreriaSpring.excepciones.EditorialServiceException;
import libreria.spring.LibreriaSpring.repositorios.EditorialRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Matias Luca Soto
 */
@Service
public class EditorialService {

    //ATRIBTOS - REPOSITORIOS
    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Transactional
    public void crearNuevaEditorial(String nombre) throws EditorialServiceException {

        nombre = nombre.trim();
        verificar(nombre);

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

    @Transactional
    public void modificarEditorial(String idEditorial, String nombre) throws EditorialServiceException {

        nombre = nombre.trim();
        verificar(nombre);

        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();

            if (editorial.getNombre().equals(nombre)) {
                throw new EditorialServiceException("Debe ingresar un nombre diferente al actual.");
            } else if (editorialRepositorio.buscarPorNombre(nombre) != null) {
                throw new EditorialServiceException("El nombre de editorial que intenta modificar ya existe.");
            }

            editorial.setNombre(nombre);
            editorial.setAlta(true);
            editorialRepositorio.save(editorial);
        } else {
            throw new EditorialServiceException("No se ha encontrado la editorial solicitada.");
        }
    }

    @Transactional
    public void darBaja(String idEditorial) throws EditorialServiceException {

        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setAlta(false);
            editorialRepositorio.save(editorial);
        } else {
            throw new EditorialServiceException("No se ha encontrado la editorial solicitada.");
        }
    }

    @Transactional
    public void darAlta(String idEditorial) throws EditorialServiceException {

        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setAlta(true);
            editorialRepositorio.save(editorial);
        } else {
            throw new EditorialServiceException("No se ha encontrado la editorial solicitada.");
        }
    }

    @Transactional(readOnly = true)
    public Editorial buscarPorId(String id) throws EditorialServiceException {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new EditorialServiceException("No se ha encontrado la editorial solicitada.");
        }
    }

    @Transactional(readOnly = true)
    public List<Editorial> listarTodos() throws EditorialServiceException {
        try {
            return editorialRepositorio.findAll();
        } catch (Exception e) {
            throw new EditorialServiceException("Hubo un problema para traer todas las editoriales.");
        }
    }

    @Transactional
    protected Editorial buscarCrearEditorial(String nombre) throws EditorialServiceException {

        nombre = nombre.trim();
        verificar(nombre);

        Editorial editorial = editorialRepositorio.buscarPorNombre(nombre);
        if (editorial == null) {
            editorial = new Editorial();
            editorial.setNombre(nombre);
            editorial.setAlta(true);
            return editorialRepositorio.save(editorial);
        } else {
            return editorial;
        }
    }
    
     protected Editorial modificarEditorialDesdeLibro(String nombreActual, String nombreNuevo) throws EditorialServiceException {
        
        nombreNuevo = nombreNuevo.trim();
        verificar(nombreNuevo);
        
        Editorial editorial = editorialRepositorio.buscarPorNombre(nombreActual);
        if (nombreNuevo.equals(nombreActual)) {
            return editorial;
        } else {
            editorial.setNombre(nombreNuevo);
            editorial.setAlta(true);
            return editorialRepositorio.save(editorial);
        }
        
    }

    private void verificar(String nombre) throws EditorialServiceException {
        if (nombre.isEmpty() || nombre == null) {
            throw new EditorialServiceException("El nombre de la editorial no puede estar vacío.");
        }
    }

}
