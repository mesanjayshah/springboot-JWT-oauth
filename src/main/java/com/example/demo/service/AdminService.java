package com.example.demo.service;

import com.example.demo.entity.Administrator;
import com.example.demo.entity.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AdminService {

    Administrator saveAdmin(Administrator admin);
    Role saveRole(Role role);
    void addRoleToAdmin(String username, String roleName);
    Administrator getAdminByUsername(String username);
    List<Administrator> getAdmins();

}
