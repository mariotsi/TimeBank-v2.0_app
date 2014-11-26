package me.mariotti.timebank.classes;


public class User {
    public int id;
    public String email;
    public String username;
    public boolean is_active;
    public boolean is_admin;
    public int available_hours;
    public int worked_hours;
    public int requested_hours;
    public int used_hours;
    public String address;
    public int city;

    public User(int id, String email, String username, boolean is_active, boolean is_admin, int available_hours, int worked_hours, int requested_hours, int used_hours, String address, int city) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.is_active = is_active;
        this.is_admin = is_admin;
        this.available_hours = available_hours;
        this.worked_hours = worked_hours;
        this.requested_hours = requested_hours;
        this.used_hours = used_hours;
        this.address = address;
        this.city = city;
    }
}
