/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kris
 */
public class UsersData {

    private static final String usersFileName = "users.xml";

    private final XStream xs;

    private final HashMap<String, User> users;

    public UsersData() {
        users = new HashMap<>();

        xs = new XStream(new DomDriver());
        xs.alias("user", User.class);
        loadUsersFromXml();
    }

    public synchronized boolean addUser(String userName, String passWord, int privilegeLevel) {
        if (!isRegisteredUser(userName)) {
            User user = new User(userName, passWord, privilegeLevel);
            users.put(userName, user);
            return true;
        }
        return false;
    }

    public synchronized boolean isRegisteredUser(String userName) {
        return users.containsKey(userName);
    }

    public synchronized User findUser(String userName) {
        if (!isRegisteredUser(userName)) {
            return null;
        }
        return users.get(userName);
    }

    private void loadUsersFromXml() {
        FileInputStream in = null;
        ObjectInputStream  usersIn = null;
        try {
            in = new FileInputStream(usersFileName);
            usersIn = xs.createObjectInputStream(in);

            while (true) {
                User user = (User) usersIn.readObject();
                users.put(user.getUsername(), user);
            }

        } catch (FileNotFoundException ex) {
            
        } catch (EOFException end) {

        } catch (IOException ex) {
            Logger.getLogger(BankData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BankData.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(usersIn != null) {
                    usersIn.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(BankData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void saveUsersToXml() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(usersFileName);
            ObjectOutputStream output = xs.createObjectOutputStream(out, "users");
            users.entrySet().stream().forEach((entry) -> {
                try {
                    output.writeObject(entry.getValue());
                } catch (IOException ex) {
                    Logger.getLogger(BankData.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BankData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BankData.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(BankData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
