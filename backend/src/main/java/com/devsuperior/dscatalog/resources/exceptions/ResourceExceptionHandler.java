package com.devsuperior.dscatalog.resources.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

/**
 * Essa anotação permite que essa classe intercepte alguma exceção que aconteça
 * na camada de resource (controller) e é ela que vai tratar essa exceção.
 **/
@ControllerAdvice
public class ResourceExceptionHandler {

	/**
	 * Esse método será uma reposta de requisão onde o conteúdo da repostas vai ser
	 * do tipo StandarErro Pois, assim, consigo produzir uma estrutura customizada
	 * de erro! Para ele funcionar, ele tem que receber 2 parâmetros, uma exceção do
	 * tipo notfoundException, e um objeto do tipo httpservelet pois ele tem as
	 * informações da requisição. Esse método, para interceptar o controler e tratar a exceção, 
	 * ele tem que ter a anotation @ExceptionHandler, passando o argumento nome da exceção.class
	 * Sempre que acontecer em algum dos controladores rest, alguma exeção do tipo entityNotFound, o tratamento
	 * dessa exceção será redirecionada para esse método.   
	 **/
	@ExceptionHandler(ResourceNotFoundException.class) 
	public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException e, HttpServletRequest request) {

		HttpStatus status = HttpStatus.NOT_FOUND;
		
		StandardError err = new StandardError();
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("Resultado não encontrado :(");
		err.setMessage(e.getMessage());
		err.setPath(request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(DataBaseException.class) 
	public ResponseEntity<StandardError> dataBase(DataBaseException e, HttpServletRequest request) {
		
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		StandardError err = new StandardError();
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("Exceção no banco de dados");
		err.setMessage(e.getMessage());
		err.setPath(request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}

}
