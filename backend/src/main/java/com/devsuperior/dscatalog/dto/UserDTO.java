package com.devsuperior.dscatalog.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.devsuperior.dscatalog.entities.User;

public class UserDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	
	@NotBlank(message="Campo obrigatório")
	private String firstName;
	private String lastName;
	
	@Email(message = "Favor entrar com um e-mail válido")
	private String email;

	Set<RoleDTO> roles = new HashSet<>();

	public UserDTO() {
	}

	public UserDTO(Long id, String firstName, String lastName, String email) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public UserDTO(User entity) {
		this.id = entity.getId();
		this.firstName = entity.getFirstName();
		this.lastName = entity.getLastName();
		this.email = entity.getEmail();
		
		/**No user entity eu coloquei um parâmetro no manyToMany (fetch), com isso, como já
		 * está mapeando pelo JPA eu forço que sempre que buscar um usuário no banco
		 * os roles já virão pendurados no usuário. Com isso, a entidade User eu já possui os objetos de
		 * roles pendurados nele. Com isso, no roleDto eu crio um construtor RoleDTO que recebe uma entidade role
		 * a partir disso eu acesso a entidade, dentro da entidade eu dou um get na lista de roles que já veio
		 * junto com a entidade user. A partir disso eu faço um foreach, pegando cada elemento role, dou um new
		 * transformando ele em roleDTO e adicionando na lista de Roles do objeto UserDTO, agrupando os 2 objetos**/		
		entity.getRoles().forEach(role -> this.roles.add(new RoleDTO(role)));

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<RoleDTO> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleDTO> roles) {
		this.roles = roles;
	}

}
