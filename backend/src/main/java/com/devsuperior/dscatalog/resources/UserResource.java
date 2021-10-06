package com.devsuperior.dscatalog.resources;

import java.net.URI;

import javax.validation.Valid;

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

import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.services.UserService;

@RestController
@RequestMapping(value = "/users") /** Mapeando a rota rest do recurso. É colocada no plural **/
public class UserResource {

	@Autowired
	private UserService service;

	/**
	 * É um objeto do spring que encapsula uma resposta http. Ele é do tipo generic
	 * e podemos definir qual o tipo de dado que estará no corpo da resposta http
	 **/

	@GetMapping
	public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
		Page<UserDTO> list = service.findAllPaged(pageable);
		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id) { // @PathVariable - associa a variavel da rota
		UserDTO dto = service.findById(id);
		return ResponseEntity.ok().body(dto);
	}

	/**
	 * Para que o endpoint reconheça o objeto enviado na requisão e case com o
	 * prodDto, precisamos colocar o @RequestBody. Preciso adicionar também
	 * o @POstMapping, pois no padrão REST, quando vou inserir eu preciso usar o
	 * post enão o get como é feito no findAll e findById.
	 **/
	@PostMapping
	public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto) {

		UserDTO newDto = service.insert(dto);

		/**
		 * O correto é retornar o 201 que é recurso criado e não 200 que o padrão. Para
		 * sermos mais fiéis ao padrão REST, preciamos passar mais um parâmtro adicional
		 * no cabeçalho da reposta além do 201, que é o cabeçalho(Header) Desta forma,
		 * usamos o objeto URI e passamos o caminho + o parâmetro (id) do recurso que
		 * acabou de ser criado.
		 **/
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newDto.getId()).toUri();

		return ResponseEntity.created(uri).body(newDto);

	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@Valid @PathVariable Long id, @RequestBody UserDTO prodDto) {
		prodDto = service.update(id, prodDto);
		return ResponseEntity.ok().body(prodDto);

	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<UserDTO> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
