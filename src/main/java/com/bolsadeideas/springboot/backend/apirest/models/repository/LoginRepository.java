package com.bolsadeideas.springboot.backend.apirest.models.repository;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login,Integer> {
}
