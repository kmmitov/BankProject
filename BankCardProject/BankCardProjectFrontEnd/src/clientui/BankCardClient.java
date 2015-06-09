/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientui;

import controller.ServerInterface;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Kris
 */
public class BankCardClient {
    
    private ServerInterface server;
    
    private JFrame currentWindow;
    
    private String sessionId = null;
    
    
    private boolean loggedOn = false;
    
    public void changeSession(String sessionInfo) {
        if(sessionInfo == null) {
            this.sessionId  = null;
            this.loggedOn = false;
            
            if(currentWindow != null) 
                currentWindow.setVisible(false);
            currentWindow = new JFrame();
            currentWindow.add(new LoginPanel(this));
        } else {
            this.sessionId = sessionInfo;
            this.loggedOn = true;
            if(currentWindow != null)
                currentWindow.setVisible(false);
            currentWindow = new JFrame();
            currentWindow.add(new ServicePanel(this));
            
        }
        attachHandlers(currentWindow,loggedOn);
        currentWindow.setSize(600,400);
        currentWindow.setVisible(true);
    }
    
    public String getSessionInfo() {
        return sessionId;
    }
  
    
    public BankCardClient() {
        initRMI();
        changeSession(null);
    }
    
    public void initRMI() {
        
        try {
            Registry registry = LocateRegistry.getRegistry("", 1089);
            server = (ServerInterface)registry.lookup("bankserver");
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(BankCardClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ServerInterface getServer() {
        return server;
    }
    
    public static void main(String[] args) {
        BankCardClient client = new BankCardClient();
    }

    private void attachHandlers(JFrame currentWindow, boolean loggedOn) {
        currentWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if(loggedOn) {
            currentWindow.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    try {
                        server.closeConnection(sessionId);
                        changeSession(sessionId);
                    } catch (RemoteException ex) {
                        Logger.getLogger(BankCardClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        } else {
            currentWindow.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                   System.exit(0);
                }
            });
        }
    }
    
}
