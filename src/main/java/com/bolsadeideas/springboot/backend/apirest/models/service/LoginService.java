package com.bolsadeideas.springboot.backend.apirest.models.service;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Login;
import java.util.List;

public interface LoginService {

    public List<Login>findAll();
}
