package com.grupo_ciencia.parkingadminapp;

/**
 * Created by benji on 3/10/17.
 */

public class Parking {

    private boolean status;
    private int spaces_quantity;
    private String  id_parking;
    private String date;

    public String getName_parking() {
        return name_parking;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName_parking(String name_parking) {
        this.name_parking = name_parking;
    }

    private String name_parking;

    public String getId_parking() {
        return id_parking;
    }

    public void setId_parking(String id_parking) {
        this.id_parking = id_parking;
    }

    public String getName_admin() {
        return name_admin;

    }

    public void setName_admin(String name_admin) {
        this.name_admin = name_admin;
    }

    public String getLast_name_admin() {
        return last_name_admin;
    }

    public void setLast_name_admin(String last_name_admin) {
        this.last_name_admin = last_name_admin;
    }

    private String name_admin;
    private String last_name_admin;


    public int getSpaces_quantity() {
        return spaces_quantity;
    }

    public void setSpaces_quantity(int spaces_quantity) {
        this.spaces_quantity = spaces_quantity;
    }

    public boolean isStatus() {
        return status;

    }
    public void setStatus(boolean status) {
        this.status = status;
    }
}
