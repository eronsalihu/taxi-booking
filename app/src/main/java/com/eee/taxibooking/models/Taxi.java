package com.eee.taxibooking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Taxi {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("number_1")
    @Expose
    private String number1;
    @SerializedName("number_2")
    @Expose
    private String number2;
    @SerializedName("noCallPayment")
    @Expose
    private String noCallPayment;

    public Taxi() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNumber1() {
        return number1;
    }

    public void setNumber1(String number1) {
        this.number1 = number1;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number2) {
        this.number2 = number2;
    }

    public String getNoCallPayment() {
        return noCallPayment;
    }

    public void setNoCallPayment(String noCallPayment) {
        this.noCallPayment = noCallPayment;
    }

}