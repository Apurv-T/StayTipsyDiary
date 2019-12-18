package com.example.projectapp;

public class RegistrationValidator {

    public boolean emailIsValid(String email){
        //checks for if email is valid
        return email.matches("^[a-zA-Z0-9_+&*-]+(?:\\."+"[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" +"A-Z]{2,7}$");
    }

    public boolean passwordsMatch(String pass, String confirmPass){
        return (pass.equals(confirmPass));
    }
    public boolean passwordIsStrong(String pass){
        PasswordValidator check = new PasswordValidator();
        check.setPassword(pass);
        int strength = check.validate();
        return(strength >= 3);
    }
}
