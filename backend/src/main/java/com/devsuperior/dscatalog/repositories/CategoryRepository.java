package com.devsuperior.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.entities.Category;

/**Repository é uma interface e extende o JPA Repsitory que vem do Spring Data
 * O JPARepository espera 2 parâmetros, o tipo da e o tipo do ID.**/


/**Quando uso a anotação @Repository, os objetos do tipo CategoryRepository passam a ser gerenciados pelo Spring **/
@Repository /** Camada de persistência **/
public interface CategoryRepository extends JpaRepository<Category, Long>{

}
