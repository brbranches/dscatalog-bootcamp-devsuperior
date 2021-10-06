package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

/**
 * Essa @ registra essa classe como um componente que vai participar do sistema
 * de injeção de dependência automatizado do Spring. O Spring que vai gerenciar
 * as instâncias das dependências dos objetos do tipo CategoryService
 **/
@Service
public class CategoryServices {

	/**
	 * Para acessar as categorias no banco, preciso que meu CategoryService tenha
	 * uma dependência com o CategoryRepository E preciso anotar o
	 * CategoryRepository com @Repository para falar que ele vai ser injetado aqui.
	 * Além disso, preciso anotar o repository aqui com @Autowired para que aqui
	 * dentro do service eu tenha injetada uma instância do CategoryRepository sem a
	 * necessidade de instânciar um objeto do tipo CategoryRepository dando um new
	 * nela. O Spring faz isso de forma automática.
	 **/

	@Autowired
	private CategoryRepository repository;

	/**
	 * Transactional= quando alguma operação envolve transação com o banco, podemos
	 * fazer a anotação @Transactional, isso faz com que a transação só ocorra se o
	 * processo estiver 100%, resguardando de possíveis problemas. Como se fosse um
	 * try catch com rollback. O próprio spring gerencia isso. O readOnly = true,
	 * evita que trave o banco para ler os dados, fazendo com que ele rode
	 * normalmente mesmo em tempo de execuçaõ. Sempre utilizar readonly em
	 * transações de leitura de banco.
	 **/
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		Page<Category> list = repository.findAll(pageable);

		/**
		 * Como a camada controladora não pode ter acesso à camada de entidade Category,
		 * eu preciso converter a lista de category em uma lista de categoryDTO, então,
		 * eu instancia uma lista de category DTO e faço um for, onde eu adiciono oso
		 * objetos da entidade Category dentro da lista de CategoryDTO, convertendo-os
		 * para DTO, onde ai sim, poderemos trafegar os dados para a camada controladora
		 * 
		 * ENTIDADE --> DTO --> CONTROLLET
		 *
		 * List<CategoryDTO> listDTO = new ArrayList<>(); for (Category category : list)
		 * { listDTO.add(new CategoryDTO(category)); }
		 * 
		 * 
		 * MAS VAMOS UTILIZAR NO FORMATO ABAIXO, USANDO LAMBDA
		 **/

		/**
		 * Posso utilizar também o stream e o map.O stream permite trabalhar com funções
		 * de alta ordem, incusive lambda. O Map, transforma cada elemento original em
		 * uma outra coisa, nesse caso, transformamos cada elemento da lista Category em
		 * um elemento da lista CategoryDTO. O resultado é um stream, então preciso
		 * converter de volta em uma lista com o collectors.tolist().
		 **/

		return list.map(x -> new CategoryDTO(x));

	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {

		/**
		 * O optional vai receber uma um optional da entidade vinda do banco, por isso a
		 * chamamos de obj O objeto Optional evita que seja trabalhado valor nulo, ou
		 * seja, o retorno dessa busca nunca será nulo.
		 **/
		Optional<Category> obj = repository.findById(id);

		/**
		 * Recebendo a entidade vinda do objeto optional o enttity recebe o obj, este
		 * tenta acessar o objeto que está nele, que é o category, se o category não
		 * existir eu vou instanciar uma exceção, lançando essa mensagem para o
		 * entityNotFound, e este lança para a superclasse RuntimeException o argumento
		 * (que é essa mensagem aqui), tudo isso, usando o orElseThrow
		 **/
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Resultado não encontrado :("));

		/**
		 * Como meu método retorna um DTO, eu preciso instanciar um Category DTO e
		 * passar a entidade como argumento
		 **/
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO catDto) {

		Category entity = new Category();
		entity.setName(catDto.getName());
		entity = repository.save(entity); // o save retorna uma referência para a entidade salva

		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO catDto) {
		try {
			Category entity = repository.getOne(id);
			entity.setName(catDto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);

		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não existe " + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id não existe " + id);

		} catch (DataIntegrityViolationException e1) {
			throw new DataBaseException("Violação de integridade no banco de dados");
		}

	}
}
