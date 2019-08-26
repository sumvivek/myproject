package com.vikash.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.net.*;
import java.io.Reader.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vikash.modal.User;
import com.vikash.repository.UserRepository;

@Service
@Transactional
public class UserService {
	 //HttpServletResponse res;
	@Autowired
	private  UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	
	public void add(User user ) {
		userRepository.save(user);
	}
	
	public List<User> getAll(){
		List<User> users = new ArrayList<User>();
		userRepository.findAll().forEach(users::add);
		
		return users;
	}
	
	public void delete(int id) {
		userRepository.delete(id);
	}

	public User findByUsernameAndPassword(String username, String password) {
		// TODO Auto-generated method stub
		return userRepository.findByUsernameAndPassword(username,password);
	}

	public User getById(int id) {
		// TODO Auto-generated method stub
		return userRepository.findById(id);
	}
	
	 // private String key="AIzaSyD6z0kV4vNsl_9S6M5jskALaR7nSwoAn_Q";

	   // private String cx="017529225481606875724:vl1n1u7vhhi";

	    private boolean enabled;

	    public void setEnabled(boolean enabled) {
	        this.enabled = enabled;
	    }

	    public boolean isEnabled() {
	        return enabled;
	    }

		public void search(String query) throws Exception {

			String key="AIzaSyD6z0kV4vNsl_9S6M5jskALaR7nSwoAn_Q";
		     int count=0;
		    URL url = new URL(
		            "https://www.googleapis.com/customsearch/v1?key="+key+ "&cx=013036536707430787589:_pqjad5hr1a&q="+ query+ "&alt=json");
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setRequestMethod("GET");
		    conn.setRequestProperty("Accept", "application/json");
		    BufferedReader br = new BufferedReader(new InputStreamReader(
		            (conn.getInputStream())));
		    
		   
		    

		    String output;
		    System.out.println("Output from Server .... \n");
		    
		    while ((output = br.readLine()) != null) {
                   
		        if(output.contains("\"link\": \"")){ 
		        	 count++;
		            String link=output.substring(output.indexOf("\"link\": \"")+("\"link\": \"").length(), output.indexOf("\","));
		            
		          // System.out.println("<html><body><h1>"+link+"<h1></body></html></br>");
		            System.out.println(link);     
		        } 
		       
		    }
		   
		   System.out.println("count:"+count);
		    conn.disconnect();                              
		}
	     
}
