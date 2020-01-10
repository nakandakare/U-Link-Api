package com.bolsadeideas.springboot.app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.springboot.app.models.entity.Usuario;
import com.bolsadeideas.springboot.app.models.services.IUsuarioService;

@CrossOrigin("http://localhost:4200") //Activo CORS
@RestController
@RequestMapping("/api")
public class UsuarioRestController {
	
	@Autowired
	private IUsuarioService usuarioService;
	
	//Utilizo Bcrypt para codificar la contraseña
	@Autowired
	private BCryptPasswordEncoder bc;

	@GetMapping("/usuarios")
	public List<Usuario> index() {
		return usuarioService.findAll();
	}
	
	@GetMapping("/usuarios/{id}")
	public Usuario show(@PathVariable Long id) {
		return usuarioService.findById(id);
	}
	
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public Usuario create(@RequestBody Usuario usuario) {
		String plainPassword = usuario.getPassword();
		String hashedPassword = bc.encode(plainPassword); //Implemento Bcrypt.
		usuario.setPassword(hashedPassword); //Seteo la contraseña con Bcrypt.
		return usuarioService.save(usuario);
	}
	
	@PostMapping("/usuarioPorEmail")
	public Usuario usuarioPorEmail(String email) {
		Usuario usuario = usuarioService.findWithEmail(email);
		return usuario;
	}
	
	@PostMapping("/setImg")
	public void setImg(@RequestParam("img") MultipartFile img, @RequestParam("id") long id) throws IOException {
		
		Path directorioRecursos = Paths.get("src//main//resources//static//upload");
		String rootPath = directorioRecursos.toFile().getAbsolutePath();
		byte[] bytes = img.getBytes();
		Path rutaCompleta = Paths.get(rootPath + "//" + img.getOriginalFilename());
		Files.write(rutaCompleta, bytes);
		/*Usuario usuarioActual = usuarioService.findById(id);
		usuarioActual.setData(img.getBytes());
		usuarioService.save(usuarioActual);*/
		
		
	}
	
	/*Utilizo autentication api con Token en vez de esta api.
	 * 
	 * @PostMapping("/login")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public Usuario login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		Usuario usuario = usuarioService.findWithEmail(email);
		
		if(usuario.getEmail().equals(email) && usuario.getPassword().equals(password)) {
			return usuario;
		} else {
			response.sendError(400,"No coincide la contraseña");
			return null;
		} 
		
	}*/
	
	@PutMapping("/usuarios/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Usuario update(@RequestBody Usuario usuario, @PathVariable Long id) {
		
		Usuario usuarioActual = usuarioService.findById(id);
		
		usuarioActual.setNombre(usuario.getNombre());
		usuarioActual.setApellido(usuario.getApellido());
		usuarioActual.setEmail(usuario.getEmail());
		usuarioActual.setImg(usuario.getImg());
		usuarioActual.setRole(usuario.getRole());
		
		return usuarioService.save(usuarioActual); //En caso de que uso el "save" con Objeto con un ID, en vez "insertar", el "save" hace un MERGE. osea que actualiza.
	}
	
	@DeleteMapping("/usuarios/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		usuarioService.delete(id);
	}
	
}




