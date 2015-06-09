/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import data.BankData;
import data.User;
import data.UsersData;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

enum Operations { TOKENIZE, DETOKENIZE };

/**
 *
 * @author Kris
 */
public class ServerController extends UnicastRemoteObject
                              implements ServerInterface {
    
    
    private final HashMap<String,Session> sessions;
    
    private final BankData cardData;
    
    private final UsersData userData;
    
    private final Tokenizer tokenizer;
    
    public ServerController() throws RemoteException {
        
        sessions = new HashMap<>();
        userData = new UsersData();
        cardData = new BankData();
        tokenizer = new Tokenizer(cardData);
        register();
    }

    @Override
    public String createConnection(String userName, String passWord) throws RemoteException {
        if(userData.isRegisteredUser(userName)) {
            User user = userData.findUser(userName);
            if(validateUser(user,passWord)) {
                String sessionId = new Date().toString() + userName;
                Session newSession = new Session(sessionId, user);
                sessions.put(sessionId,newSession);
                return sessionId;
            }
            else {
                return "Error! Password is incorrect!";
            }
        }
        return "Error! No such user exists!";
    }

    @Override
    public synchronized String tokenize(String sessionId, String cardNumber) throws RemoteException {
        if(!sessions.containsKey(sessionId)) {
            return "Error! Session has timed out!";
        }
        if(validateOperation(sessions.get(sessionId),Operations.TOKENIZE)) {
            String token = tokenizer.tokenize(cardNumber);
            if(token == null) {
                return "Error! Card number is not valid!";
            }
            cardData.addPair(cardNumber, token);
            return token;
        } else {
            return "Error! You are not authorized to tokenize cards!";
        }
    }

    @Override
    public synchronized String detokenize(String sessionId, String token) throws RemoteException {
        if(!sessions.containsKey(sessionId)) {
            return "Error! Session has timed out!";
        }
        if(validateOperation(sessions.get(sessionId),Operations.DETOKENIZE)) {
            String cardNumber = tokenizer.detokenize(token);
            if(cardNumber == null) {
                return "Error! Token has not been registered!";
            }
            return cardNumber;
        } else {
            return "Error! You are not authorized to detokenize cards!";
        }
    }

    /**
     *
     * @param sessionId
     * @return
     * @throws RemoteException
     */
    @Override
    public String closeConnection(String sessionId) throws RemoteException {
        if(sessions.containsKey(sessionId)) {
            sessions.remove(sessionId);
            return "Connection successfully closed!";
        }
        return "Error! No such session exists!";
    }
    
    public boolean addUser(String username, String password, int privilegeLevel) {
        if(userData.isRegisteredUser(username)) {
            return false;
        }
        userData.addUser(username, password, privilegeLevel);
        return true;
    }
    
    public BankData getBankData() { return cardData; }
    
    public UsersData getUserData() { return userData; }
    
    private boolean validateUser(User user, String passWord) {
        return user.getPassword().equals(passWord);
    }

    private boolean validateOperation(Session session, Operations operation) {
        if(operation == Operations.TOKENIZE) {
            return session.getUser().getPrivilegeLevel() >= cardData.getTokenizeMinimum();
        } else {
            return session.getUser().getPrivilegeLevel() >= cardData.getDetokenizeMinimum();
        }
    }

    private void register() {
        try {
            Registry registry = LocateRegistry.createRegistry(1089);
            registry.rebind("bankserver", this);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
