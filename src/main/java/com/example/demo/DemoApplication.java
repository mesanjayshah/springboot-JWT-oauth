package com.example.demo;

import com.example.demo.entity.Administrator;
import com.example.demo.entity.Role;
import com.example.demo.service.AdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(AdminService adminService) {
		return args -> {
			adminService.saveRole(new Role(null, "ROLE_ADMIN"));
			adminService.saveRole(new Role(null, "ROLE_USER"));

			adminService.saveAdmin(new Administrator(null, "Sanjay Shah", "sanjay31", "sanjay31", "Kathmandu", new ArrayList<>()));
			adminService.saveAdmin(new Administrator(null, "Naresh Sharma", "naresh31", "naresh31", "Butwal", new ArrayList<>()));

			adminService.addRoleToAdmin("sanjay31", "ROLE_ADMIN");
			adminService.addRoleToAdmin("naresh31", "ROLE_USER");
		};
	}

}
