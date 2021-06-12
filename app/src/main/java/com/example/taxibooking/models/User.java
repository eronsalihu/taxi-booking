package com.example.taxibooking.models;

public class User {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private String dateOfBirth;

    public User() {
    }

    public User(String fullName, String email,
                String password, String phone, String gender,
                String dateOfBirth) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return fullName;
    }


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }


}
