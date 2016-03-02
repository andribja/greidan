package com.greidan.greidan.greidan;

import java.io.Serializable;

/**
 * Created by Daníel on 01/03/2016.
 */
public class User implements Serializable {

    int id;
    String username;
    String email;

    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
