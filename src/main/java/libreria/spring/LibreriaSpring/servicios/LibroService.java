package libreria.spring.LibreriaSpring.servicios;

import java.util.List;
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
        verificar(isbn, titulo, anio, ejemplares, ejemplaresPrestados, nombreAutor, nombreEditorial, true);

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
        verificar(isbn, titulo, anio, ejemplares, ejemplaresPrestados, nombreAutor, nombreEditorial, false);

        //Buscamos el Libro a modificar
        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);

        if (respuesta.isPresent()) {

            Libro libro = respuesta.get();

            verificarCambios(libro, isbn, titulo, anio, ejemplares, ejemplaresPrestados, nombreAutor, nombreEditorial);

            //Seteamos los atributos
            libro.setIsbn(isbn);
            libro.setTitulo(titulo);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);
            libro.setEjemplaresPrestados(ejemplaresPrestados);
            libro.setEjemplaresRestantes(ejemplares - ejemplaresPrestados);
            libro.setAlta(true);

            //Seteamos Autor y Editorial
            libro.setAutor(autorService.modificarAutorDesdeLibro(libro.getAutor().getNombre(), nombreAutor));
            libro.setEditorial(editorialService.modificarEditorialDesdeLibro(libro.getEditorial().getNombre(), nombreEditorial));

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

    @Transactional
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

    @Transactional(readOnly = true)
    public Libro buscarPorId(String id) throws LibroServiceException {
        Optional<Libro> respuesta = libroRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new LibroServiceException("No se ha encontrado el libro solicitado.");
        }
    }

    @Transactional(readOnly = true)
    public List<Libro> listarTodos() throws LibroServiceException {
        try {
            return libroRepositorio.findAll();
        } catch (Exception e) {
            throw new LibroServiceException("Hubo un problema para traer todos los libros.");
        }
    }

    /**
     * Método privado para verificar todos los nuevos atributos de un Libro y lanzar una excepción si alguno no es válido según diferentes criterios
     *
     * @param isbn Si es un nuevo libro, no puede estar repetido. Tampoco puede estar null
     * @param titulo Si es un nuevo libro, no puede estar repetido. Tampoco puede estar null
     * @param anio No puede ser mayor al año actual ni estar null
     * @param ejemplares Su valor restado la cantidad de prestados no puede ser menor a 0
     * @param ejemplaresPrestados Su valor restandolo con el total no puede ser menor a 0
     * @param nombreAutor No puede estar vacío ni null
     * @param nombreEditorial No puede estar vacío ni null
     * @param nuevoLibro Boolean para determinar si es un nuevo libro (true) o estamos modificando uno ya existente (false)
     * @throws LibroServiceException
     */
    @Transactional(readOnly = true)
    private void verificar(Long isbn, String titulo, Integer anio, Integer ejemplares, Integer ejemplaresPrestados, String nombreAutor, String nombreEditorial, boolean nuevoLibro) throws LibroServiceException {

        if (libroRepositorio.buscarPorIsbn(isbn) != null && nuevoLibro) {
            throw new LibroServiceException("El ISBN ingresado ya pertenece a otro libro.");
        } else if (isbn == null) {
            throw new LibroServiceException("El ISBN no puede estar vacío.");
        }

        if (libroRepositorio.buscarPorTitulo(titulo) != null && nuevoLibro) {
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

    /**
     * Verificación de que se haya ingresado al menos un cambio en el formulario de modifica libro. Si todos los argumentos recibidos son idénticos a los atributos
     * actuales del libro, se larga una excepción con el mensaje de que no se ha registrado ningún cambio
     * @param libro
     * @param isbn
     * @param titulo
     * @param anio
     * @param ejemplares
     * @param ejemplaresPrestados
     * @param nombreAutor
     * @param nombreEditorial
     * @throws LibroServiceException Si no se detecta ningún cambio en el formulario de Libro 
     */
    private void verificarCambios(Libro libro, Long isbn, String titulo, Integer anio, Integer ejemplares, Integer ejemplaresPrestados, String nombreAutor, String nombreEditorial) throws LibroServiceException {
        if (libro.getIsbn().equals(isbn) && libro.getTitulo().equals(titulo) && libro.getAnio().equals(anio) && libro.getEjemplares().equals(ejemplares) && libro.getEjemplaresPrestados().equals(ejemplaresPrestados) && libro.getAutor().getNombre().equals(nombreAutor) && libro.getEditorial().getNombre().equals(nombreEditorial)) {
            throw new LibroServiceException("No se ha registrado ningún cambio.");
        }
    }
}
