package com.bolsadeideas.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bolsadeideas.springboot.app.auth.filter.JWTAuthenticationFilter;
import com.bolsadeideas.springboot.app.auth.filter.JWTAuthorizationFilter;
import com.bolsadeideas.springboot.app.models.services.JpaUserDetailService;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private JpaUserDetailService userDetailService;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
				//Para utilizar esta autenticacion (que para mi es Magia) como API y con JWT también hay que hacer clases Filters donde le envias usernames password con post y luego utiliza este metodo.  
	@Autowired //aca se define como se autentica el usuario (En este caso se utiliza autenticacion con JPA).
	public void configurerGlobal(AuthenticationManagerBuilder build) throws Exception	{
		build.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
	}

	/*@Override //Para permitir el Post Request.
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring().antMatchers(HttpMethod.POST, "/api/usuarios");
	}*/
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/api/login","/api/register").permitAll()
		//	.anyRequest().authenticated()
		/*.and()
		.formLogin().permitAll()
		.and()
		.logout().permitAll()*/
		.and()
		.addFilter(new JWTAuthenticationFilter(authenticationManager()))
		.addFilter(new JWTAuthorizationFilter(authenticationManager()))
		.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //Desabilito el manejo de sesion para que sea STATELESS AUTHENTICAITON.
	}
	
	
}
