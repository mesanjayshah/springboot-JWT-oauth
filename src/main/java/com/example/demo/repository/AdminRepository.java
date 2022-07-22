package com.example.demo.repository;

import com.example.demo.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Administrator, Long> {

    Administrator findByUsername(String username);

}


