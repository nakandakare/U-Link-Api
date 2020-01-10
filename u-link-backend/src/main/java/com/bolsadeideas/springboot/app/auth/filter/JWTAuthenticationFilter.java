package com.bolsadeideas.springboot.app.auth.filter;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bolsadeideas.springboot.app.models.dao.IUsuarioDao;
import com.bolsadeideas.springboot.app.models.entity.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

//Api para autenticacion Login
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	//Va a trabajar en conjunto con JPA UserDetailService para hacer la autenticacion del login y lo utilizo junto con el return.
	private AuthenticationManager authenticationManager;
	
	//Creo una key estatica para utilizar con al creacion de JWToken
	public static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	
	//Para obtener el usuario por email y asi poder enviar datos relevantes del usaurio mediante el token.
	@Autowired
	IUsuarioDao usuarioDao;
	
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
		/* authResult = authToken autenticado, obtenemos el email */
		String email = ((User) authResult.getPrincipal()).getUsername();
		String emailModificado = email.split("@")[0]; //se lo envio como subject através del token.
		
		/* Como el Filter no utiliza Spring Context, no puedo usar DI (por ej, Autowired a IUsuarioDao retorna null),
		por lo tanto utilizo servletContext y de esta manera utilizo usuarioDao. */
        if(usuarioDao==null){
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            usuarioDao = webApplicationContext.getBean(IUsuarioDao.class);
        }
        //Retorno datos del usuario para enviarlo con el token (El id).
        Usuario usuario = usuarioDao.findByEmail(email);
        
		// Aca creamos el Jason web token.
		String token = Jwts.builder()
						.setSubject(emailModificado)
						.signWith(SECRET_KEY) //Key estatico
						.setExpiration(new Date(System.currentTimeMillis() + 36000))
						.claim("id", usuario.getId())
						.claim("nombre", usuario.getNombre())
						.claim("apellido", usuario.getApellido())
						.claim("img", usuario.getImg())
						.compact(); //Compactar para crear el token.
					
		//Pasamos token por header como respuesta cuando se autentica. Se envia con prefijo "bearer" como estandar.
		response.addHeader("	", "Bearer " + token);

		Map<String, Object> body = new HashMap<String, Object>(); //respuesta json
		body.put("token", token);
		body.put("nombre", usuario.getNombre());
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
