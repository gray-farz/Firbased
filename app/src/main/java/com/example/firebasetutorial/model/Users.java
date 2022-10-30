package com.example.firebasetutorial.model;

import com.example.firebasetutorial.factory.Datas;

public class Users implements Datas
{
    String user,pass,email,number;

    public Users(String user, String pass, String email, String number) {
        this.user = user;
        this.pass = pass;
        this.email = email;
        this.number = number;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
