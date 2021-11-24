package libreria.spring.LibreriaSpring.servicios;

import java.util.ArrayList;
import java.util.Calendar;
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

    /**
     * Método para crear una nueva instancia de la entidad Libro con todos sus atributos pasados por parámetro y persistido en la base de datos
     *
     * @param isbn Atributo de la nueva entidad
     * @param titulo Atributo de la nueva entidad
     * @param anio Atributo de la nueva entidad
     * @param ejemplares Atributo de la nueva entidad
     * @param idAutor Atributo de la nueva entidad
     * @param idEditorial Atributo de la nueva entidad
     * @throws LibroServiceException Si alguno de los atributos es inválido
     * @throws AutorServiceException Si no se logra encontrar el Autor con el idAutor recibido como argumento
     * @throws EditorialServiceException Si no se encuentra la Editorial con el idEditorial recibido como argumento
     */
    @Transactional
    public void crearLibro(Long isbn, String titulo, Integer anio, Integer ejemplares, String idAutor, String idEditorial) throws LibroServiceException, AutorServiceException, EditorialServiceException {

        //Removemos espacios innecesarios
        titulo = titulo.trim();

        //Validamos los parámetros
        verificar(isbn, titulo, anio, ejemplares, idAutor, idEditorial, true);

        //Creación del Objeto Libro
        Libro libro = new Libro();

        //Seteamos los atributos
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setAnio(anio);
        libro.setEjemplares(ejemplares);
        libro.setEjemplaresPrestados(0);
        libro.setEjemplaresRestantes(ejemplares);
        libro.setAlta(true);

        //Seteamos Autor y Editorial buscando ambas instancias con los Services de sus Clases
        libro.setAutor(autorService.buscarPorId(idAutor));
        libro.setEditorial(editorialService.buscarPorId(idEditorial));

        //Persistimos el libro en la base de datos
        libroRepositorio.save(libro);
    }

    /**
     * Método para modificar los atributos de una entidad Libro ya guardada en la base de datos y persistir los cambios
     *
     * @param idLibro De la entidad a actualizar
     * @param isbn Atributo actualizado o igual al actual
     * @param titulo Atributo actualizado o igual al actual
     * @param anio Atributo actualizado o igual al actual
     * @param ejemplares Atributo actualizado o igual al actual
     * @param idAutor Atributo actualizado o igual al actual
     * @param idEditorial Atributo actualizado o igual al actual
     * @throws LibroServiceException Si algún atributo no cumple los requisitos o si no se detecta ningún cambio en relación a los atributos actuales
     * @throws AutorServiceException Si no se encuentra la entidad
     * @throws EditorialServiceException Si no se encuentra la entidad
     */
    @Transactional
    public void modificarLibro(String idLibro, Long isbn, String titulo, Integer anio, Integer ejemplares, String idAutor, String idEditorial) throws LibroServiceException, AutorServiceException, EditorialServiceException {

        //Removemos espacios innecesarios
        titulo = titulo.trim();

        //Validamos los parámetros
        verificar(isbn, titulo, anio, ejemplares, idAutor, idEditorial, false);

        //Buscamos el Libro a modificar
        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);
        if (respuesta.isPresent()) {

            //Traemos la entidad
            Libro libro = respuesta.get();

            //Verificamos los nuevos atributos frente a los actuales
            verificarCambios(libro, isbn, titulo, anio, ejemplares, idAutor, idEditorial);

            //Seteamos los atributos
            libro.setIsbn(isbn);
            libro.setTitulo(titulo);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);    
            libro.setEjemplaresRestantes(ejemplares - libro.getEjemplaresPrestados());
            libro.setAlta(true);

            //Seteamos Autor y Editorial
            libro.setAutor(autorService.buscarPorId(idAutor));
            libro.setEditorial(editorialService.buscarPorId(idEditorial));

            //Persistimos el libro en la base de datos
            libroRepositorio.save(libro);
        } else {
            throw new LibroServiceException("No se ha encontrado el libro solicitado.");
        }
    }

    /**
     * Método para dar de baja una entidad Libro en la base de datos y deshabilitarla de préstamos nuevos
     *
     * @param idLibro De la entidad a dar de baja
     * @throws LibroServiceException Si no se encuentra la entidad solicitada
     */
    @Transactional
    public void darBaja(String idLibro) throws LibroServiceException {

        //Se busca a la instancia que tenga el id pasado como parámetro
        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);

        //Si se lo encuentra, se settea la baja y se persiste
        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setAlta(false);
            libroRepositorio.save(libro);
        } else {
            throw new LibroServiceException("No se ha encontrado el libro solicitado.");
        }
    }

    /**
     * Método para dar de alta una entidad Libro en la base de datos y rehabilitarla en como libro disponible para prestar
     *
     * @param idLibro
     * @throws LibroServiceException
     */
    @Transactional
    public void darAlta(String idLibro) throws LibroServiceException {

        //Se busca la instancia en la base de datos según el id
        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);

        //Si se lo encuentra, se settea el alta y se persiste
        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setAlta(true);
            libroRepositorio.save(libro);
        } else {
            throw new LibroServiceException("No se ha encontrado el libro solicitado.");
        }
    }

    /**
     * Busca y devuelve la instancia de Libro en la base de datos que corresponda al id pasado como argumento
     *
     * @param id De la instancia a buscar
     * @return La instancia de Libro con el id correspondiente
     * @throws LibroServiceException Si no se encuentra ninguna entidad
     */
    @Transactional(readOnly = true)
    public Libro buscarPorId(String id) throws LibroServiceException {
        Optional<Libro> respuesta = libroRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new LibroServiceException("No se ha encontrado el libro solicitado.");
        }
    }

    /**
     * Busca y devuelve TODAS las instancias de Libro en la base de datos, sin discriminación
     *
     * @return Una List con todos los Libros y sus atributos de la base de datos
     * @throws LibroServiceException Si hubo algún problema trayendo la Lista
     */
    @Transactional(readOnly = true)
    public List<Libro> listarTodos() throws LibroServiceException {
        try {
            return libroRepositorio.findAll();
        } catch (Exception e) {
            throw new LibroServiceException("Hubo un problema para traer todos los libros. Por favor, reintente nuevamente.");
        }
    }

    /**
     * Busca y devuelve una Lista con todos los Libros activos en la base de datos
     * @return Una List de Libros con su atributo Alta en true
     * @throws LibroServiceException Si hubo algún problema para traer a la lista de la base de datos
     */
    public List<Libro> listarActivos() throws LibroServiceException {
        try {
            List<Libro> todos = listarTodos();
            List<Libro> activos = new ArrayList();
            for (Libro libro : todos) {
                if (libro.getAlta()) {
                    activos.add(libro);
                }
            }
            return activos;
        } catch (LibroServiceException e) {
            throw new LibroServiceException("Hubo un problema para traer todos los libros. Por favor, reintente nuevamente.");
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
     * @param idAutor No puede estar vacío ni null
     * @param idEditorial No puede estar vacío ni null
     * @param nuevoLibro Boolean para determinar si es un nuevo libro (true) o estamos modificando uno ya existente (false)
     * @throws LibroServiceException
     */
    @Transactional(readOnly = true)
    private void verificar(Long isbn, String titulo, Integer anio, Integer ejemplares, String idAutor, String idEditorial, boolean nuevoLibro) throws LibroServiceException {

        //El ISBN no puede pertenecer a otro Libro ni tampoco estar null
        if (libroRepositorio.buscarPorIsbn(isbn) != null && nuevoLibro) {
            throw new LibroServiceException("El ISBN ingresado ya pertenece a otro libro.");
        } else if (isbn == null) {
            throw new LibroServiceException("El ISBN no puede estar vacío.");
        }

        //El título no puede pertenecer a otro Libro, ni estar vacío ni null
        if (libroRepositorio.buscarPorTitulo(titulo) != null && nuevoLibro) {
            throw new LibroServiceException("El libro con el titulo ingresado ya existe.");
        } else if (titulo.isEmpty() || titulo == null) {
            throw new LibroServiceException("El titulo del libro no puede estar vacío.");
        }

        //El año no puede ser mayor al actual ni estar null
        if (anio > Calendar.getInstance().get(Calendar.YEAR)) {
            throw new LibroServiceException("El año ingresado es inválido.");
        } else if (anio == null) {
            throw new LibroServiceException("El año no puede estar vacío.");
        }

        //La cantidad de ejemplares no puede estar null o ser menor a 1 ejemplar
        if (ejemplares == null || ejemplares < 1) {
            throw new LibroServiceException("Debe indicar una cantidad mínima y válida de ejemplares.");
        }

        //El id del Autor no puede estar vacío ni null
        if (idAutor.isEmpty() || idAutor == null) {
            throw new LibroServiceException("El Autor no puede estar vacio.");
        }

        //El id de la Editorial no puede estar vacío ni null
        if (idEditorial.isEmpty() || idEditorial == null) {
            throw new LibroServiceException("La Editorial no puede estar vacia.");
        }
    }

    /**
     * Verificación de que se haya ingresado al menos un cambio en el formulario de modifica libro, y de que los nuevos atributos sean válidos frente a los atributos anteriores de la entidad Libro a modificar
     *
     * @param libro Entidad a modificar
     * @param isbn Atributo actualizado o idéntico al anterior
     * @param titulo Atributo actualizado o idéntico al anterior
     * @param anio Atributo actualizado o idéntico al anterior
     * @param ejemplares Atributo actualizado o idéntico al anterior
     * @param ejemplaresPrestados Atributo actualizado o idéntico al anterior
     * @param idAutor Atributo actualizado o idéntico al anterior
     * @param idEditorial Atributo actualizado o idéntico al anterior
     * @throws LibroServiceException Si no se detecta ningún cambio en el formulario de Libro o alguno de los nuevos atributos no cumplen los requisitos
     */
    private void verificarCambios(Libro libro, Long isbn, String titulo, Integer anio, Integer ejemplares, String idAutor, String idEditorial) throws LibroServiceException {
        
        //Si no se detecta ningún cambio en los nuevos atributos, se le indicará al Usuario con el mensaje de la excepción que debe introducir al menos un cambio
        if (libro.getIsbn().equals(isbn) && libro.getTitulo().equals(titulo) && libro.getAnio().equals(anio) && libro.getEjemplares().equals(ejemplares) && libro.getAutor().getId().equals(idAutor) && libro.getEditorial().getId().equals(idEditorial)) {
            throw new LibroServiceException("No se ha registrado ningún cambio.");
        }
        
        //La nueva cantidad de ejemplares no puede ser menor a la cantidad de ejemplares que están actualmente prestados
        if (ejemplares < libro.getEjemplaresPrestados()) {
            throw new LibroServiceException("La cantidad de ejemplares no puede ser menor a la cantidad de prestados (" + libro.getEjemplaresPrestados() + " prestados.)");
        }
        
        //El nuevo ISBN no puede pertenecer a otra instancia de Libro diferente a la que se va a modificar
        if (libroRepositorio.buscarPorIsbn(isbn) != null && !(libro.getIsbn().equals(isbn))) {
            throw new LibroServiceException("El ISBN ingresado ya pertenece a otro libro");
        }
        
        //El nuevo título no puede pertenecer a otra instancia de Libro diferente a la que se va a modificar
        if (libroRepositorio.buscarPorTitulo(titulo) != null && !(libro.getTitulo().equals(titulo))) {
            throw new LibroServiceException("El título ingresado ya pertenece a otro libro");
        }
    }
}
