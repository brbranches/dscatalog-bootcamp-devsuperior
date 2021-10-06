package com.devsuperior.dscatalog.services;



import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	private Long idExistente;
	private Long idNaoExistente;
	private Long idComDependencia;
	private PageImpl<Product> page;
	private Product product;
	private Category category;
	ProductDTO productDTO;

	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	void setUp() throws Exception {
		idExistente = 1L;
		idNaoExistente = 2L;
		idComDependencia = 3L; //Id onde alguma entidad depende dele;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		category = Factory.createCategory();
		productDTO = Factory.createProductDTO();

		//Com o ArgumentMatcher eu posso na chamada, simular qualquer valor do tipo que eu quiser. Nesse caso, quando eu chamar o findAl passando qualquer valor
		
		Mockito.when(repository.getOne(idExistente)).thenReturn(product);
		Mockito.when(repository.getOne(idNaoExistente)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(categoryRepository.getOne(idExistente)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(idNaoExistente)).thenThrow(EntityNotFoundException.class);
	
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(product)); //of instancia um optional com product dentro
		Mockito.when(repository.findById(idNaoExistente)).thenReturn(Optional.empty()); //empty instancia um optional sem nada dentro
		Mockito.doNothing().when(repository).deleteById(idExistente);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(idNaoExistente);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idComDependencia);
	};

	@InjectMocks
	private ProductService service;
	
	@Test
	public void updateDeveRetornarResourceNotFoundExceptionQuandoOIdNaoExistir() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(idNaoExistente, productDTO);
		});
	}
	
	@Test
	public void updateDeveRetornarProductDTOQuandoIdExistir() {
		ProductDTO result = service.update(idExistente, productDTO);
		Assertions.assertNotNull(result);
	}
	
	
	@Test
	public void findByIdDeveRetornarUmProductDTOQuandoOIdExistir() {
		ProductDTO result = service.findById(idExistente);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findByIdDeveRetornarResourceNotFoundExceptionQuandoOIdNaoExistir() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			service.findById(idNaoExistente);
			
		});
		
	}

	@Test
	public void findAllPagedDeveRetornarUmaPagina() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(pageable);
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable); 
		//Mockito.times (1) ele verifica se o findAll(pageable) foi chamado 1 vez dentro do findAllPaged do service
	}
	

	@Test
	public void deleteNaoDeveFazerNadaQuandoIDExistir() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(idExistente);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(idExistente);
	}

	@Test
	public void deleteDeveRetornarResourceNotFoundExceptionQuandoIdNaoExistir() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(idNaoExistente);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(idNaoExistente);
	}
	
	@Test
	public void deleteDeveRetornarDatabaseExceptionQuandoIdNaoExiste() {
		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(idComDependencia);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(idComDependencia);
	}
	


}
