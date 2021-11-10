package libreria.spring.LibreriaSpring.servicios;

import java.util.Optional;
import libreria.spring.LibreriaSpring.entidades.Libro;
import libreria.spring.LibreriaSpring.excepciones.AutorServiceException;
import libreria.spring.LibreriaSpring.excepciones.EditorialServiceException;
import libreria.spring.LibreriaSpring.excepciones.LibroServiceException;
import libreria.spring.LibreriaSpring.repositorios.LibroRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Matias Luca Soto
 */
@Service
public class LibroService {

    //ATRIBUTOS REPOSITORIOS
    @Autowired
    private LibroRepositorio libroRepositorio;

    //ATRIBUTOS SERVICES
    @Autowired
    private AutorService autorService;
    @Autowired
    private EditorialService editorialService;

    @Transactional
    public void crearLibro(Long isbn, String titulo, Integer anio, Integer ejemplares, Integer ejemplaresPrestados, String nombreAutor, String nombreEditorial) throws LibroServiceException, AutorServiceException, EditorialServiceException {

        //Removemos espacios innecesarios
        titulo = titulo.trim();
        nombreAutor = nombreAutor.trim();
        nombreEditorial = nombreEditorial.trim();

        //Validamos los parámetros
        verificar(isbn, titulo, anio, ejemplares, ejemplaresPrestados, nombreAutor, nombreEditorial);

        //Creación del Objeto Libro
        Libro libro = new Libro();

        //Seteamos los atributos
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setAnio(anio);
        libro.setEjemplares(ejemplares);
        libro.setEjemplaresPrestados(ejemplaresPrestados);
        libro.setEjemplaresRestantes(ejemplares - ejemplaresPrestados);
        libro.setAlta(true);

        //Seteamos Autor y Editorial
        libro.setAutor(autorService.buscarCrearAutor(nombreAutor));
        libro.setEditorial(editorialService.buscarCrearEditorial(nombreEditorial));

        //Persistimos el libro en la base de datos
        libroRepositorio.save(libro);
    }

    @Transactional
    public void modificarLibro(String idLibro, Long isbn, String titulo, Integer anio, Integer ejemplares, Integer ejemplaresPrestados, String nombreAutor, String nombreEditorial) throws LibroServiceException, AutorServiceException, EditorialServiceException {

        //Removemos espacios innecesarios
        titulo = titulo.trim();
        nombreAutor = nombreAutor.trim();
        nombreEditorial = nombreEditorial.trim();

        //Validamos los parámetros
        verificar(isbn, titulo, anio, ejemplares, ejemplaresPrestados, nombreAutor, nombreEditorial);

        //Buscamos el Libro a modificar
        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);

        if (respuesta.isPresent()) {

            Libro libro = respuesta.get();

            //Seteamos los atributos
            libro.setIsbn(isbn);
            libro.setTitulo(titulo);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);
            libro.setEjemplaresPrestados(ejemplaresPrestados);
            libro.setEjemplaresRestantes(ejemplares - ejemplaresPrestados);
            libro.setAlta(true);

            //Seteamos Autor y Editorial
            libro.setAutor(autorService.buscarCrearAutor(nombreAutor));
            libro.setEditorial(editorialService.buscarCrearEditorial(nombreEditorial));

            //Persistimos el libro en la base de datos
            libroRepositorio.save(libro);
        } else {
            throw new LibroServiceException("No se ha encontrado el libro solicitado.");
        }
    }

    @Transactional
    public void darBaja(String idLibro) throws LibroServiceException {

        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setAlta(false);
            libroRepositorio.save(libro);
        } else {
            throw new LibroServiceException("No se ha encontrado el libro solicitado.");
        }
    }

    public void darAlta(String idLibro) throws LibroServiceException {

        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setAlta(true);
            libroRepositorio.save(libro);
        } else {
            throw new LibroServiceException("No se ha encontrado el libro solicitado.");
        }
    }

    @Transactional
    public void verificar(Long isbn, String titulo, Integer anio, Integer ejemplares, Integer ejemplaresPrestados, String nombreAutor, String nombreEditorial) throws LibroServiceException {

        if (libroRepositorio.buscarPorIsbn(isbn) != null) {
            throw new LibroServiceException("El ISBN ingresado ya pertenece a otro libro.");
        } else if (isbn == null) {
            throw new LibroServiceException("El ISBN no puede estar vacío.");
        }

        if (libroRepositorio.buscarPorTitulo(titulo) != null) {
            throw new LibroServiceException("El libro con el titulo ingresado ya existe.");
        } else if (titulo.isEmpty() || titulo == null) {
            throw new LibroServiceException("El titulo del libro no puede estar vacío.");
        }

        if (anio > 2021) {
            throw new LibroServiceException("El año ingresado es inválido.");
        } else if (anio == null) {
            throw new LibroServiceException("El año no puede estar vacío.");
        }

        if (ejemplares - ejemplaresPrestados < 0) {
            throw new LibroServiceException("La cantidad de ejemplares prestados es mayor a la cantidad total de ejemplares.");
        } else if (ejemplares == null || ejemplaresPrestados == null) {
            throw new LibroServiceException("Debe indicar una cantidad válida de ejemplares totales y prestados.");
        }

        if (nombreAutor.isEmpty() || nombreAutor == null) {
            throw new LibroServiceException("El nombre del Autor no puede estar vacio.");
        }

        if (nombreEditorial.isEmpty() || nombreEditorial == null) {
            throw new LibroServiceException("El nombre de la Editorial no puede estar vacio.");
        }
    }
}
