package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;

	private Long idExistente;
	private Long idNaoExistente;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		idExistente = 1L;
		idNaoExistente = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void deleteDeveDeletarOObjetoQuandoOIdExiste() {
		repository.deleteById(idExistente);
		Optional<Product> result = repository.findById(idExistente);
		Assertions.assertFalse(result.isPresent());
	}
	
	
	@Test
	public void saveDevePersistirAutoIncrementandoQuandoOIdForNulo() {
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts+1, product.getId());
		
	}

	
	@Test
	public void findByIdDeveRetornarOptionalNaoVazioQuandoIdExistir() {
		Optional<Product> result = repository.findById(idExistente);
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdDeveRetornarOptionalVazioQuandoIdNaoExistir() {
		Optional<Product> result = repository.findById(idNaoExistente);
		Assertions.assertTrue(result.isEmpty());
	}
	
	
	@Test
	public void deleteDeveLancarEmptyResultDataAccessExceptionQuandoIDNaoExiste() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(idNaoExistente);
		});
	}
	


}
