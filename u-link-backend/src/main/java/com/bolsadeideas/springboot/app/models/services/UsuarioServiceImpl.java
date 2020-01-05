package com.bolsadeideas.springboot.app.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.models.dao.IUsuarioDao;
import com.bolsadeideas.springboot.app.models.entity.Usuario;

@Service
public class UsuarioServiceImpl implements IUsuarioService{
	
	@Autowired
	IUsuarioDao usuarioDao;
	
	@Transactional(readOnly = true)
	public List<Usuario> findAll() {
		return (List<Usuario>) usuarioDao.findAll();
	}
	
	@Transactional(readOnly = true)
	public Usuario findById(Long id) {
		return usuarioDao.findById(id).orElse(null);
	}
	
	@Transactional
	public Usuario save(Usuario usuario) {
		return usuarioDao.save(usuario);
	}
	
	@Transactional
	public void delete(Long id) {
		usuarioDao.deleteById(id);
	}

	@Transactional(readOnly = true)
	public Usuario findWithEmail(String email) {
		
		return usuarioDao.findByEmail(email);
	}
}
