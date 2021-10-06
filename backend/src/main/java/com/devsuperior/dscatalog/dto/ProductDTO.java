package com.devsuperior.dscatalog.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class ProductDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	@Size(min = 5, max = 60, message = "Nome deve conter entre 5 e 60 caracteres")
	@NotBlank(message = "Campo obrigatório")
	private String name;
	
	@NotBlank(message = "Campo obrigatório")
	private String description;
	
	@Positive(message = "O preço deve ser um valor maior que R$0,00")
	private Double price;
	private String imgUrl;
	
	@PastOrPresent(message = "A data de cadastro não pode ser maior que a data de hoje")
	private Instant date;

	/**
	 * Como um produto pode ter várias categorias, utiilzaremos uma lista. Ela será
	 * do tipo CategoryDTO pois vamos associar com outros DTOs.
	 **/
	private List<CategoryDTO> categories = new ArrayList<>();

	public ProductDTO() {
	}

	public ProductDTO(Long id, String name, String description, Double price, String imgUrl, Instant date) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imgUrl = imgUrl;
		this.date = date;
	}

	/** Construtor para receber uma entidade - como foi feito com category **/
	public ProductDTO(Product entity) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.description = entity.getDescription();
		this.price = entity.getPrice();
		this.imgUrl = entity.getImgUrl();
		this.date = entity.getDate();
	}

	/**
	 * Esse cosntrutor que recebe entidade e as catregorias, é para que, quando eu
	 * chamar esse cosntrutor, eu quero instanciar o DTO, colocando cada elemento da
	 * coleçaõ de entidades Set<Category>, dentro da lista List<CategoryDTO>
	 * categories.
	 **/
	public ProductDTO(Product entity, Set<Category> categories) {
		this(entity); // Chamando o construtor acima que executa tudo que está dentro.

		/**
		 * Pra cada elemento entity da Set<Category>, eu vou na lista de categoias
		 * List<Category> e adiciono nele uma entity transformada em DTO
		 **/
		categories.forEach(cat -> this.categories.add(new CategoryDTO(cat)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}

}