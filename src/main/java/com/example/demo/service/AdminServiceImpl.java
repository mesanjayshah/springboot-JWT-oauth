package com.example.demo.service;

import com.example.demo.entity.Administrator;
import com.example.demo.entity.Role;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j @Transactional
public class AdminServiceImpl implements AdminService, UserDetailsService {

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Administrator administrator = adminRepository.findByUsername(username);
        if (administrator == null) {
            throw new UsernameNotFoundException(username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        administrator.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

        return new org.springframework.security.core.userdetails.User(
                administrator.getUsername(), administrator.getPassword(), authorities);
    }

    public Administrator saveAdmin(Administrator admin) {
        log.info("Saving admin: {}", admin);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving role: {}", role);
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToAdmin(String username, String roleName) {
        log.info("Adding role {} to admin {}", roleName, username);
        Administrator admin = adminRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        admin.getRoles().add(role);
        adminRepository.save(admin);
    }

    @Override
    public Administrator getAdminByUsername(String username) {
        log.info("Getting admin by username: {}", username);
        return adminRepository.findByUsername(username);
    }

    @Override
    public List<Administrator> getAdmins() {
        log.info("Getting all admins");
        return adminRepository.findAll();
    }

}
