package com.vikash.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.api.services.customsearch.model.Result;
import com.vikash.Exception.ImageSearchException;
import com.vikash.modal.User;

import com.vikash.services.UserService;



@org.springframework.web.bind.annotation.RestController
public class RestController {
	
	@Autowired
	private UserService userService;
	
     @CrossOrigin(origins="http://localhost:3000")
	@RequestMapping(method=RequestMethod.POST,value="user/add")
	public void add(@RequestBody User user) {
		userService.add(user);
		System.out.print("registration successfull");
	}
       
     @CrossOrigin(origins="http://localhost:3000")
     @RequestMapping("/Alluser")
 	public List<User>get() {
 		return userService.getAll();
 	}
     @CrossOrigin(origins="http://localhost:3000")
     @RequestMapping(method=RequestMethod.DELETE,value="user/{id}")
 	public void delete(@PathVariable int id) {
 		userService.delete(id);
 	}
     @CrossOrigin(origins="http://localhost:3000")
     @RequestMapping("user/{username}/{password}")
     public User  getByUsernameAndPassword(@PathVariable("username") String  username,@PathVariable("password") String password)
     {
    	 return userService.findByUsernameAndPassword(username,password);
    	 
     }
     @RequestMapping("/user/{id}")
     public User getById(@PathVariable int id)
     {
    	 return userService.getById(id);
     }
    /* @RequestMapping("/search/{username}")
     public List<Result> search(@PathVariable String  username)  
 	{
 		return userService.search(username);
 	}*/
     @CrossOrigin(origins="http://localhost:3000")
     @RequestMapping("/search/{query}")
     public void Search(@PathVariable String  query) throws Exception
     {
    	 userService.search(query);
    	// System.out.println("Query value : " + query);
		//userServices.search(query);
     }
}
