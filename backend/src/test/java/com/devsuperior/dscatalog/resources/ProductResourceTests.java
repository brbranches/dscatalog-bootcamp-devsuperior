package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private  ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private long idExistente;
	private long idNaoExistente;
	private long idDependente;
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page; //instancia PageImpl, um objeto concreto. Uso o impl ao invés de page pq da pra instanciar ele com new

	
	
	@BeforeEach
	void setUp() throws Exception{
	
		idExistente = 1;
		idNaoExistente = 2;
		
		productDTO = Factory.createProductDTO();
		page = new PageImpl<ProductDTO>(List.of(productDTO));
		
		//Quando eu chamar no service o findAllPage com qualquer argumento, eu vou retornar um obejto page do tipo
		//PageImpl ProductDTO
		when(service.findAllPaged(any())).thenReturn(page);
	
		when (service.findById(idExistente)).thenReturn(productDTO);
		when (service.findById(idNaoExistente)).thenThrow(ResourceNotFoundException.class);
		
		when (service.update(eq(idExistente), any())).thenReturn(productDTO);
		when (service.update(eq(idNaoExistente), any())).thenThrow(ResourceNotFoundException.class);
		
		when (service.insert(any())).thenReturn(productDTO);
		
		doNothing().when(service).delete(idExistente);
		doThrow(ResourceNotFoundException.class).when(service).delete(idNaoExistente);
		doThrow(DataBaseException.class).when(service).delete(idDependente);
		
	}
	
	@Test
	public void deleteDeveRetornarNoContentQuandoIdExistir() throws Exception {
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", idExistente)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());
	}
	
	
	
	@Test
	public void deleteDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception{
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", idNaoExistente)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	
	
	@Test
	public void insertDeveRetornarProductDTOCreated() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(post("/products/")
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	
	@Test
	public void updateDeveRetornarProductDTOQUandoIdExiste() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", idExistente)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void updateDeveRetornarNotFoundQUandoIdNaoExiste() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
				
				ResultActions result = 
						mockMvc.perform(put("/products/{id}", idNaoExistente)
								.content(jsonBody)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON));
				
				result.andExpect(status().isNotFound());
	}
	
	
	@Test
	public void findAllDeveRetornarUmPage() throws Exception {
		//Chamando uma requisição com método get HTTP no caminho products e espero que o status da resposta seja OK, que é o 200.
		
		ResultActions result = 
				mockMvc.perform(get("/products")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
	}
	
	@Test
	public void findByIdDeveRetornarProdutoQuandoIdExiste() throws Exception {
		//Chamando uma requisição com método get HTTP no caminho products e espero que o status da resposta seja OK, que é o 200.
				ResultActions result = 
						mockMvc.perform(get("/products/{id}",idExistente)
								.accept(MediaType.APPLICATION_JSON));
				
				result.andExpect(status().isOk());
				result.andExpect(jsonPath("$.id").exists());
				result.andExpect(jsonPath("$.name").exists());
				result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findByIdDeveRetornarNotFoundQuandoIdNaoExiste() throws Exception {
		//Chamando uma requisição com método get HTTP no caminho products e espero que o status da resposta seja OK, que é o 200.
				ResultActions result = 
						mockMvc.perform(get("/products/{id}",idNaoExistente)
								.accept(MediaType.APPLICATION_JSON));
				
				result.andExpect(status().isNotFound());
			}

}
