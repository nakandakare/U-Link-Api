package com.bolsadeideas.springboot.app.auth.filter;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.bolsadeideas.springboot.app.models.services.IUsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	//Va a trabajar en conjunto con JPA UserDetailService para hacer la autenticacion del login y lo utilizo junto con el return.
	private AuthenticationManager authenticationManager;
	
	//Creo una key estatica para utilizar con al creacion de JWToken
	public static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	
	//Para obtener el usuario por email y asi poder enviar datos relevantes del usaurio.
	@Autowired
	IUsuarioService usuarioService;
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		
		//Cuando hay un request a esta ruta, se ejecuta este filter
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login","POST"));
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		String username = obtainUsername(request);
		String password = obtainPassword(request);

		if (username == null) {
			username = "";
		}

		if (password == null) {
			password = "";
		}
		
		username = username.trim();
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,password);
		
		//El authenticationManager.authenticate() se va a encargar de autenticar utilizando JpaUserDetailService.
		return authenticationManager.authenticate(authToken);
	}

	//Este metodo se ejecuta automaticamente (ya que es un filter) cuando se autentica correctamente.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// authResult = authToken autenticado.
		
		// Aca creamos el Jason web token.
		String username = ((User) authResult.getPrincipal()).getUsername();
		String token = Jwts.builder()
						.setSubject(username)
						.signWith(SECRET_KEY)
						.setExpiration(new Date(System.currentTimeMillis() + 36000))
						.compact(); //Compactar para crear el token.
		
					
		//Pasamos token por header como respuesta cuando se autentica. Se envia con prefijo "bearer" como estandar.
		response.addHeader("Authorization", "Bearer " + token);

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("token", token);
		body.put("email", ((User) authResult.getPrincipal()).getUsername());
		body.put("Mensaje", "Hola usuario, has iniciado sesión con exito");
		
		//ObjectMapper convierte objecto java en JSON.
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(200);
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*"); //Cors
	}
	
	//Este metodo se ejecuta automaticamente (ya que es un filter) cuando no se autentica correctamente.
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("Message", "Error de autenticacion: email o password incorrecto");
		body.put("error", failed.getMessage());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setHeader("Access-Control-Allow-Origin", "*"); //Cors
	}
	
	
	
}
