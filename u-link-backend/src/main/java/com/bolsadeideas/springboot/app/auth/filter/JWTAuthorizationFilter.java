package com.bolsadeideas.springboot.app.auth.filter;

import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.IOException;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException, java.io.IOException{
		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Bearer ")) { // Verifica si el header esta correcto.
			chain.doFilter(request, response); //Va a la siguiente filter o sale del filter (en este caso sale ya que no hay mas filter)
			return;
		}

		boolean validoToken;
		Claims token = null;
		
		try { //Verifica si el token tiene algun error, en caso de que haya error ejecuta la excepcion
			token = Jwts.parser().setSigningKey(JWTAuthenticationFilter.SECRET_KEY)
					.parseClaimsJws(header.replace("Bearer ", "")).getBody();
			validoToken = true;
		} catch (JwtException | IllegalArgumentException e) {
			validoToken = false;
		}
		
		UsernamePasswordAuthenticationToken auToken = null;
		
		if(validoToken) {
			String email = token.getSubject();
			
			Collection<? extends GrantedAuthority> authorities = null;
			
			auToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
		}
		
		SecurityContextHolder.getContext().setAuthentication(auToken); //Setea datos para que pueda authenticar con esos datos.
		chain.doFilter(request, response); //Va a la siguiente filter o sale del filter (en este caso sale ya que no hay mas filter)
	}

}
