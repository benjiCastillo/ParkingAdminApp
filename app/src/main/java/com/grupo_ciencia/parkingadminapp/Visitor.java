package com.grupo_ciencia.parkingadminapp;

/**
 * Created by benji on 21/11/17.
 */

public class Visitor {
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Visitor(String date, int quantity) {
        this.date = date;
        this.quantity = quantity;
    }
    public Visitor(){}
    private String date;
    private int quantity;

}
