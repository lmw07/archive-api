package com.SignicatTask.SignicatTask;

import org.springframework.web.bind.annotation.RestController;

import com.SignicatTask.SignicatTask.Repository.LogRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class Controller {

    private final LogRepository repo;

    public Controller(LogRepository repository){
        this.repo = repository;
    }

    @PostMapping("/upload")
    //TODO
    public String processFileUpload(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }
    

}
