package com.bolsadeideas.springboot.app.models.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.models.dao.IUsuarioDao;
import com.bolsadeideas.springboot.app.models.entity.Usuario;

import ch.qos.logback.classic.Logger;
//Para cargar y Autenticar el Usuario. Al cargar el usuario con "loadUserByUsername", Spring internamente utiliza los datos retornados para verificar si el usuario y password estan correctos (osea que lo valida)
@Service("jpaUserDetailsService")
@Transactional(readOnly=true)
public class JpaUserDetailService implements UserDetailsService {
	
	@Autowired
	private IUsuarioDao usuarioDao;
	
	private Logger logger = (Logger) LoggerFactory.getLogger(JpaUserDetailService.class);
	
	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioDao.findByEmail(email);
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		if(usuario == null) {
			logger.error("Error login: no existe el usuario: " + email);
			throw new UsernameNotFoundException("Usuario no encontrado: " + email);
		}
		
		//Con el valor retornado, spring internamente autentica el login.	
		return new User(usuario.getEmail(), usuario.getPassword(), true, true, true, true,authorities);
	}
	
}
