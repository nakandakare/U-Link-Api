package com.bolsadeideas.springboot.app.models.services;

import java.util.List;

import com.bolsadeideas.springboot.app.models.entity.Usuario;

public interface IUsuarioService {
	
	public List<Usuario> findAll();
	
	public Usuario findById(Long id);
	
	public Usuario save(Usuario usuario);
	
	public void delete(Long id);
	
	public Usuario findWithEmail(String email);

}
