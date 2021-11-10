package libreria.spring.LibreriaSpring.servicios;

import java.util.Optional;
import libreria.spring.LibreriaSpring.entidades.Autor;
import libreria.spring.LibreriaSpring.excepciones.AutorServiceException;
import libreria.spring.LibreriaSpring.repositorios.AutorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Matias Luca Soto
 */
@Service
public class AutorService {

    //ATIBUTOS - REPOSITORIOS
    @Autowired
    private AutorRepositorio autorRepositorio;

    @Transactional
    public void crearNuevoAutor(String nombre) throws AutorServiceException {

        nombre = nombre.trim();
        verificar(nombre);

        Autor autor = autorRepositorio.buscarPorNombre(nombre);
        if (autor == null) {
            autor = new Autor();
            autor.setNombre(nombre);
            autor.setAlta(true);
            autorRepositorio.save(autor);
        } else {
            throw new AutorServiceException("El autor que intenta crear ya existe.");
        }
    }

    @Transactional
    public void modificarAutor(String idAutor, String nombre) throws AutorServiceException {

        nombre = nombre.trim();
        verificar(nombre);

        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autor.setNombre(nombre);
            autor.setAlta(true);
            autorRepositorio.save(autor);
        } else {
            throw new AutorServiceException("No se ha encontrado el autor solicitado.");
        }
    }

    @Transactional
    public void darBaja(String idAutor) throws AutorServiceException {

        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autor.setAlta(false);
            autorRepositorio.save(autor);
        } else {
            throw new AutorServiceException("No se ha encontrado el autor solicitado.");
        }
    }

    @Transactional
    public void darAlta(String idAutor) throws AutorServiceException {

        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autor.setAlta(true);
            autorRepositorio.save(autor);
        } else {
            throw new AutorServiceException("No se ha encontrado el autor solicitado.");
        }
    }

    @Transactional
    protected Autor buscarCrearAutor(String nombre) throws AutorServiceException {

        nombre = nombre.trim();
        verificar(nombre);

        Autor autor = autorRepositorio.buscarPorNombre(nombre);
        if (autor == null) {
            autor = new Autor();
            autor.setNombre(nombre);
            autor.setAlta(true);
            return autorRepositorio.save(autor);
        } else {
            return autor;
        }
    }
    
    private void verificar(String nombre) throws AutorServiceException {
        if (nombre.isEmpty() || nombre == null) {
            throw new AutorServiceException("El nombre del Autor no puede estar vacio.");
        }
    }

}
