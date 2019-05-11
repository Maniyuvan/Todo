package com.example.maniy.todo;

public class User {

    private String userpk;
    private String name;
    private String email;
    private String phone;

    public User() {

    }

    public User(String userpk, String name, String email, String phone) {
        this.userpk = userpk;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getUserpk() {
        return userpk;
    }

    public void setUserpk(String userpk) {
        this.userpk = userpk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
