package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

/**
 * Essa @ registra essa classe como um componente que vai participar do sistema
 * de injeção de dependência automatizado do Spring. O Spring que vai gerenciar
 * as instâncias das dependências dos objetos do tipo UserService
 **/
@Service
public class UserService {

	/**
	 * Para acessar as produtos no banco, preciso que meu UserService tenha
	 * uma dependência com o UserRepository E preciso anotar o
	 * UserRepository com @Repository para falar que ele vai ser injetado aqui.
	 * Além disso, preciso anotar o repository aqui com @Autowired para que aqui
	 * dentro do service eu tenha injetada uma instância do UserRepository sem a
	 * necessidade de instânciar um objeto do tipo UserRepository dando um new
	 * nela. O Spring faz isso de forma automática.
	 **/

	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
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
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = repository.findAll(pageable);

		/**
		 * Como a camada controladora não pode ter acesso à camada de entidade User,
		 * eu preciso converter a lista de User em uma lista de UserDTO, então,
		 * eu instancia uma lista de User DTO e faço um for, onde eu adiciono oso
		 * objetos da entidade User dentro da lista de UserDTO, convertendo-os
		 * para DTO, onde ai sim, poderemos trafegar os dados para a camada controladora
		 * 
		 * ENTIDADE --> DTO --> CONTROLLET
		 *
		 * List<UserDTO> listDTO = new ArrayList<>(); for (User User : list)
		 * { listDTO.add(new UserDTO(User)); }
		 * 
		 * 
		 * MAS VAMOS UTILIZAR NO FORMATO ABAIXO, USANDO LAMBDA
		 **/

		/**
		 * Posso utilizar também o stream e o map.O stream permite trabalhar com funções
		 * de alta ordem, incusive lambda. O Map, transforma cada elemento original em
		 * uma outra coisa, nesse caso, transformamos cada elemento da lista User em
		 * um elemento da lista UserDTO. O resultado é um stream, então preciso
		 * converter de volta em uma lista com o collectors.tolist().
		 **/

		return list.map(x -> new UserDTO(x));

	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {

		/**
		 * O optional vai receber uma um optional da entidade vinda do banco, por isso a
		 * chamamos de obj O objeto Optional evita que seja trabalhado valor nulo, ou
		 * seja, o retorno dessa busca nunca será nulo.
		 **/
		Optional<User> obj = repository.findById(id);

		/**
		 * Recebendo a entidade vinda do objeto optional o enttity recebe o obj, este
		 * tenta acessar o objeto que está nele, que é o User, se o User não
		 * existir eu vou instanciar uma exceção, lançando essa mensagem para o
		 * entityNotFound, e este lança para a superclasse RuntimeException o argumento
		 * (que é essa mensagem aqui), tudo isso, usando o orElseThrow
		 **/
		User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Resultado não encontrado :("));

		/**
		 * Como meu método retorna um DTO, eu preciso instanciar um User DTO e
		 * passar a entidade como argumento
		 **/
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		entity.setPassword(passwordEncoder.encode(dto.getPassword())); //chamando o encoder e encriptando a senha antes de setar
		entity = repository.save(entity); // o save retorna uma referência para a entidade salva
		return new UserDTO(entity);
	}


	@Transactional
	public UserDTO update(Long id, UserDTO dto) {
		try {
			User entity = repository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new UserDTO(entity);

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
	
	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		
		/** Dentro do UserDTO (prodDto) eu tenho uma lista de categoriasDTO. Para cada elemento dessa lista de categoriasDTO
		 * eu percorro e guardo em catDto. Para cada. Depois, eu preciso clonar as informações do DTO para a entidade.
		 * Assim, eu instancio um category, instancio lá em cima o categoryRepository para poder usar o 
		 * getOne(esse getOne instancia uma entidade sem abrir transação com o banco) e passo o id da CategoryDto para a
		 * variável category. Depois disso, clomo a informação para a coleção de categorias da entidade Produto**/

		entity.getRoles().clear(); //Limpa a coleção
		
		for(RoleDTO roleDto : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDto.getId());
			entity.getRoles().add(role); 
		}
	}
}
