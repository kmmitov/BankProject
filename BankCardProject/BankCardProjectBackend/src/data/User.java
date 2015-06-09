/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author Kris
 */
public class User {
    
    private final String username;
    
    private String password;
    
    private int privilegeLevel;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPrivilegeLevel() {
        return privilegeLevel;
    }
    
    
    
    public User(String username, String password, int privilegeLevel) {
        this.username = username;
        setPassword(password);
        setPrivilegeLevel(privilegeLevel);
       
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setPrivilegeLevel(int privilegeLevel) {
        if(privilegeLevel >=0 && privilegeLevel <= 3) {
            this.privilegeLevel = privilegeLevel;
        } else {
            this.privilegeLevel = 0;
        }
    }
        
}
