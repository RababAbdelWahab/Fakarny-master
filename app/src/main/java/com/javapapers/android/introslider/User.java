package com.javapapers.android.introslider;
public class User {
    public String name;
    public String email;
    public String phone;
    public String address;
    public String relation;
    public String img;

    public User(String img, String name, String email, String phone, String address, String relation) {
        this.img = img;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.relation = relation;
    }
}
