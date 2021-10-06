package com.devsuperior.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

	@Bean
	/**é um componete do spring. Com o bean eu estou dizendo que essa instância do BCryter
	será um componente gerenciado pelo springboot, ai poderei injetar esse cara em outros componentes.**/
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
}
