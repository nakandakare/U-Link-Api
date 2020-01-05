package com.bolsadeideas.springboot.app.models.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity()
@Table(name="usuarios")
public class Usuario {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) //Autogenerar ID
	private Long id;
	
	@Column(nullable = false)
	private String nombre;
	
	@Column(nullable = false)
	private String apellido;
	
	@Column(nullable = false, unique=true)
	private String email;
	
	@Column(nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	
	private String role;
	
	@Column(name="img")
	private String img = "https://i.postimg.cc/2ygF6qVQ/user-default.jpg";
	
	@Column(name="create_at")
	@Temporal(TemporalType.DATE)
	private Date createAt;
		
	@PrePersist //se va a llamar este metodo justo antes de invocar el metodo persist
	public void prePersist() {
		createAt = new Date();
	}
	
	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	
	
}
