package com.bolsadeideas.springboot.backend.apirest.controller;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Login;
import com.bolsadeideas.springboot.backend.apirest.models.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public List<Login>index(){
        return loginService.findAll();
    }


}
