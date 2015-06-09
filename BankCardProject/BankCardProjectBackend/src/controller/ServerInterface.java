/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Kris
 */
public interface ServerInterface extends Remote {
    
    public String createConnection(String userName, String passWord) throws RemoteException;
    
    public String tokenize(String sessionId, String cardNumber) throws RemoteException;
    
    public String detokenize(String sessionId, String cardNumber) throws RemoteException;
    
    public String closeConnection(String sessionId) throws RemoteException;
    
}
