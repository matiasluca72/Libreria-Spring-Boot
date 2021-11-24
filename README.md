# Libreria-Spring-Boot
< ESPAÑOL >  <br>
Administrador web de una Libreria o Biblioteca donde se podrán administrar y realizar funciones CRUD (Create, Read, Update & Delete) en Entidades Libros unidas a Entidades Autores y Editoriales, igual de editables y customizables. <br>
Lista de acciones que se pueden realizar con los Libros:
<ul>
<li>Crear Libros con todas sus características principales sin que se repitan el Titulo y el ISBN</li>
<li>Modificar atributos de Libros ya creados</li>
<li>Elegir Autores de una lista de Autores creados Y dados de alta</li>
<li>Elegir Editoriales de una lista de Editoriales creados Y dados de alta</li>
<li>Ver el listado de todos los Libros, Autores y Editoriales con todos sus atributos y poder darlos de baja en un click</li>
<li>Establecer una cantidad de ejemplares disponibles de cada Libro</li>
</ul>
<br>
A su vez, también se podrá ingresar y tener un listado de Clientes con sus datos personales (Nombre, Apellido, DNI y Teléfono) y poder realizar préstamos a favor de un Cliente
sobre un Libro en particular. <br>
Esta funcionalidad de préstamos cuenta con los siguientes features:
<ul>
<li>Poder realizar Préstamos solo en Clientes dados de Alta y con Libros también dados de alta</li>
<li>Realizar tantos Préstamos se necesiten siempre y cuando el Libro tenga ejemplares disponibles, de lo contrario se avisa con un mensaje de error</li>
<li>Tener un listado con todos los Prestamos (tanto los pendientes como los ya devueltos) con la fecha de préstamo y la fecha de devolución (si lo hubiese)</li>
<li>Dar de baja un Préstamo (efectuar la devolución) en un solo click</li>
<li>También se pueden reactivar Préstamos antiguos si y solo si aún quedan ejemplares restantes del Libro en cuestión</li>
</ul>

<br> <hr>
Este es un proyecto hecho como ejercitación en el lenguaje <strong>Java 8</strong> utilizando el <strong>Framework Spring con Spring Boot</strong> para diseñar una
aplicación con un modelo de <strong>diseño MVC.</strong><br> <br>
En ella, podrás crear, leer, modificar, y dar de baja <strong>(CRUD)</strong> datos escenciales de Objetos como Libros, Autores, Editoriales, Clientes y Préstamos. <br>
Además, podrás realizar verdaderos préstamos de tus Libros a tus Clientes (siempre y cuando hayan existencias). <br>
Tendrás registro de la fecha en la que se realizó cada Prestamo, cuáles están pendientes de devolver y cuáles ya fueron devueltos y en qué fecha.
<br> <br>
Esta ejercitación forma parte del <strong><a href="https://carreras.eggeducacion.com/ar/programacion/" target="_blank">curso de FullStack Developer de EGG.</a></strong>
<hr>
<h2>Dependencias Maven utilizadas </h2>
<ul>
  <li>Spring Boot DevTools</li>
  <li>Spring Web</li>
  <li>Thymeleaf</li>
  <li>Spring Security</li>
  <li>Spring Data JPA</li>
  <li>MySQL Driver</li>
</ul>

<h2>Otras tecnologias utilizadas</h2>
<ul>
  <li>HTML</li>
  <li>CSS</li>
  <li>Bootstrap Components</li>
  <li>Bootstrap Themes</li>
  <li>MySQL Workbench</li>
</ul>
  
< /ESPAÑOL >
