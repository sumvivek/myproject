package com.my360face.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.my360face"})
@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) { SpringApplication.run(SecurityApplication.class, args); }
}
