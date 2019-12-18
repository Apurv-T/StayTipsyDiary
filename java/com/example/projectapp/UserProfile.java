package com.example.projectapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserProfile implements Serializable {

    private String id;
    private String username;
    private String email;
    private String status;

    public UserProfile(String uid, String username, String email, String status){
        this.id = uid;
        this.username = username;
        this.email = email;
        this.status = status;
    }
    public void setid(String uid){
        this.id = uid;
    }

    public String getid() {
        return id;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail() {return email;}

    public void setStatus(String status){this.status = status;}

    public String getStatus(){return status;}

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("username", username);
        result.put("email", email);
        result.put("status", status);

        return result;
    }
}

