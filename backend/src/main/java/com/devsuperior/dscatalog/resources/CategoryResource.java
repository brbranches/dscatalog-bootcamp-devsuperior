package com.devsuperior.dscatalog.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.services.CategoryServices;

@RestController
@RequestMapping(value = "/categories") /** Mapeando a rota rest do recurso. É colocada no plural **/

public class CategoryResource {

	@Autowired
	private CategoryServices service;

	/**
	 * É um objeto do spring que encapsula uma resposta http. Ele é do tipo generic
	 * e podemos definir qual o tipo de dado que estará no corpo da resposta http
	 **/

	@GetMapping
	public ResponseEntity<Page<CategoryDTO>> findAll(Pageable pageable) 
		 {
		
		
		Page<CategoryDTO> list = service.findAllPaged(pageable);
				
		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) { // @PathVariable - associa a variavel da rota
																			// com o parâmetro
		CategoryDTO catDto = service.findById(id);
		return ResponseEntity.ok().body(catDto);
	}

	/**
	 * Para que o endpoint reconheça o objeto enviado na requisão e case com o
	 * catDTO, precisamos colocar o @RequestBody. Preciso adicionar também
	 * o @POstMapping, pois no padrão REST, quando vou inserir eu preciso usar o
	 * post enão o get como é feito no findAll e findById.
	 **/
	@PostMapping
	public ResponseEntity<CategoryDTO> insert(@RequestBody CategoryDTO catDto) {

		catDto = service.insert(catDto);

		/**
		 * O correto é retornar o 201 que é recurso criado e não 200 que o padrão. Para
		 * sermos mais fiéis ao padrão REST, preciamos passar mais um parâmtro adicional
		 * no cabeçalho da reposta além do 201, que é o cabeçalho(Header) Desta forma,
		 * usamos o objeto URI e passamos o caminho + o parâmetro (id) do recurso que
		 * acabou de ser criado.
		 **/
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(catDto.getId()).toUri();

		return ResponseEntity.created(uri).body(catDto);

	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategoryDTO catDto) {
		catDto = service.update(id, catDto);
		return ResponseEntity.ok().body(catDto);

	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
