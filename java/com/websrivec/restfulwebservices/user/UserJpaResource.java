package com.websrivec.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import  org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class UserJpaResource {

	@Autowired
	private UserDaoService service;
	
	@Autowired
	private UserRepository repository;
	
	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers(){
		return repository.findAll();
	}
	
	@GetMapping("/jpa/users/{id}")
	public EntityModel<Optional<User>> retrieveUser(@PathVariable int id) {
		Optional<User> user = repository.findById(id);
		
		if(user==null)
			throw new UserNotFoundException("id-"+ id);
		
		
		//"all-users", SERVER_PATH + "/users"
		//retrieveAllUsers
		EntityModel<Optional<User>> resource = EntityModel.of(user);
		
		WebMvcLinkBuilder linkTo = 
				linkTo(methodOn(this.getClass()).retrieveAllUsers());
		
		resource.add(linkTo.withRel("all-users"));
		
		//HATEOAS
		
		return resource;
	}
	
	

	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable int id) {
		 repository.deleteById(id);
		
	}
	
	@PostMapping("/jpa/users")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		User savedUser = repository.save(user);
		URI location = ServletUriComponentsBuilder
		.fromCurrentRequest().path("/{id}")
		.buildAndExpand(savedUser.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> retrieveAllPosts(@PathVariable int id){
		Optional<User> userOptional = repository.findById(id);
		if(!userOptional.isPresent()) {
			throw new UserNotFoundException("id-"+id);
		}
		return userOptional.get().getPosts();
	}
//	@GetMapping(path="/hello-world-bean")
//	public HelloWorldBean helloWorldBean() {
//		return new HelloWorldBean("Hello World");
//	}
	
}
