package libreria.spring.LibreriaSpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Proyecto de gestor de Libreria Web utilizando Spring Boot conectado a una base de datos MySQL para gestionar Libros, Autores, Editoriales, Clientes y Préstamos
 * @author Matias Luca Soto
 */
@SpringBootApplication
public class LibreriaSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibreriaSpringApplication.class, args);
	}

}
