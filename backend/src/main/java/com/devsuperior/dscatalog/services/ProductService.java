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
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

/**
 * Essa @ registra essa classe como um componente que vai participar do sistema
 * de injeção de dependência automatizado do Spring. O Spring que vai gerenciar
 * as instâncias das dependências dos objetos do tipo ProductService
 **/
@Service
public class ProductService {

	/**
	 * Para acessar as produtos no banco, preciso que meu ProductService tenha
	 * uma dependência com o ProductRepository E preciso anotar o
	 * ProductRepository com @Repository para falar que ele vai ser injetado aqui.
	 * Além disso, preciso anotar o repository aqui com @Autowired para que aqui
	 * dentro do service eu tenha injetada uma instância do ProductRepository sem a
	 * necessidade de instânciar um objeto do tipo ProductRepository dando um new
	 * nela. O Spring faz isso de forma automática.
	 **/

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;

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
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		Page<Product> list = repository.findAll(pageable);

		/**
		 * Como a camada controladora não pode ter acesso à camada de entidade Product,
		 * eu preciso converter a lista de product em uma lista de productDTO, então,
		 * eu instancia uma lista de product DTO e faço um for, onde eu adiciono oso
		 * objetos da entidade Product dentro da lista de ProductDTO, convertendo-os
		 * para DTO, onde ai sim, poderemos trafegar os dados para a camada controladora
		 * 
		 * ENTIDADE --> DTO --> CONTROLLET
		 *
		 * List<ProductDTO> listDTO = new ArrayList<>(); for (Product product : list)
		 * { listDTO.add(new ProductDTO(product)); }
		 * 
		 * 
		 * MAS VAMOS UTILIZAR NO FORMATO ABAIXO, USANDO LAMBDA
		 **/

		/**
		 * Posso utilizar também o stream e o map.O stream permite trabalhar com funções
		 * de alta ordem, incusive lambda. O Map, transforma cada elemento original em
		 * uma outra coisa, nesse caso, transformamos cada elemento da lista Product em
		 * um elemento da lista ProductDTO. O resultado é um stream, então preciso
		 * converter de volta em uma lista com o collectors.tolist().
		 **/

		return list.map(x -> new ProductDTO(x));

	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {

		/**
		 * O optional vai receber uma um optional da entidade vinda do banco, por isso a
		 * chamamos de obj O objeto Optional evita que seja trabalhado valor nulo, ou
		 * seja, o retorno dessa busca nunca será nulo.
		 **/
		Optional<Product> obj = repository.findById(id);

		/**
		 * Recebendo a entidade vinda do objeto optional o enttity recebe o obj, este
		 * tenta acessar o objeto que está nele, que é o product, se o product não
		 * existir eu vou instanciar uma exceção, lançando essa mensagem para o
		 * entityNotFound, e este lança para a superclasse RuntimeException o argumento
		 * (que é essa mensagem aqui), tudo isso, usando o orElseThrow
		 **/
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Resultado não encontrado :("));

		/**
		 * Como meu método retorna um DTO, eu preciso instanciar um Product DTO e
		 * passar a entidade como argumento
		 **/
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO prodDto) {

		Product entity = new Product();
		copyDtoToEntity(prodDto, entity);
		
		entity = repository.save(entity); // o save retorna uma referência para a entidade salva

		return new ProductDTO(entity);
	}


	@Transactional
	public ProductDTO update(Long id, ProductDTO prodDto) {
		try {
			Product entity = repository.getOne(id);
			copyDtoToEntity(prodDto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);

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
	
	private void copyDtoToEntity(ProductDTO prodDto, Product entity) {
		entity.setName(prodDto.getName());
		entity.setDescription(prodDto.getDescription());
		entity.setDate(prodDto.getDate());
		entity.setImgUrl(prodDto.getImgUrl());
		entity.setPrice(prodDto.getPrice());
		
		entity.getCategories().clear();
		
		/** Dentro do ProductDTO (prodDto) eu tenho uma lista de categoriasDTO. Para cada elemento dessa lista de categoriasDTO
		 * eu percorro e guardo em catDto. Para cada. Depois, eu preciso clonar as informações do DTO para a entidade.
		 * Assim, eu instancio um category, instancio lá em cima o categoryRepository para poder usar o 
		 * getOne(esse getOne instancia uma entidade sem abrir transação com o banco) e passo o id da CategoryDto para a
		 * variável category. Depois disso, clomo a informação para a coleção de categorias da entidade Produto**/
		for(CategoryDTO catDto : prodDto.getCategories()) {
			Category category = categoryRepository.getOne(catDto.getId());
			entity.getCategories().add(category); 
		}
	}
}
