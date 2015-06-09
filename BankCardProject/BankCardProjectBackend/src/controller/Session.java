/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import data.User;

/**
 *
 * @author Kris
 */
public class Session {
   
    private String sessionId;
    
    private User user;

    public Session(String sessionId, User user) {
        this.sessionId = sessionId;
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public User getUser() {
        return user;
    }
    
    
}
