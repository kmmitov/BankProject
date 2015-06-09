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
import java.util.ArrayList;
import java.util.Formatter;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kris
 */
public class BankData {

    private static final String tokensFileName = "tokens.xml";

    private static final int TOKENIZE_MIN = 1;

    private static final int DETOKENIZE_MIN = 2;

    private final TreeMap<String, String> tokenToCardMap;

    private final TreeMap<String, ArrayList<String>> cardToTokenMap;

    private final XStream xs;

    public BankData() {
        tokenToCardMap = new TreeMap<>();
        cardToTokenMap = new TreeMap<>();
        xs = new XStream(new DomDriver());
        xs.alias("tokencard", Pair.class);
        loadPairsFromXml();
    }

    public synchronized boolean addPair(String cardNumber, String token) {
        if (!isRegistered(token)) {
            tokenToCardMap.put(token, cardNumber);
            if (cardToTokenMap.containsKey(cardNumber)) {
                ArrayList<String> tokens = cardToTokenMap.get(cardNumber);
                tokens.add(token);
                cardToTokenMap.replace(cardNumber, tokens);
            } else {
                ArrayList<String> tokens = new ArrayList<>();
                tokens.add(token);
                cardToTokenMap.put(cardNumber, tokens);
            }
            return true;
        }
        return false;
    }

    public synchronized boolean isRegistered(String token) {
        return tokenToCardMap.containsKey(token);
    }

    public synchronized String findCard(String token) {
        if (!tokenToCardMap.containsKey(token)) {
            return null;
        } else {
            return tokenToCardMap.get(token);
        }
    }

    private void loadPairsFromXml() {

        FileInputStream in = null;
        ObjectInputStream pairsIn = null;
        try {
            in = new FileInputStream(tokensFileName);

            pairsIn = xs.createObjectInputStream(in);

            while (true) {
                Pair pair = (Pair) pairsIn.readObject();

                addPair(pair.getCardNumber(), pair.getToken());
            }

        } catch (FileNotFoundException ex) {

        } catch (EOFException end) {

        } catch (IOException ex) {

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BankData.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (pairsIn != null) {
                    pairsIn.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(BankData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void printSortedByToken(Formatter output) {
        output.format("Token:         \tCard:%n");
        tokenToCardMap.entrySet().stream().forEach((_item) -> {
            output.format("%s\t%s%n", _item.getKey(), _item.getValue());
        });
    }

    public void printSortedByCard(Formatter output) {
        output.format("Token:         \tCard:%n");
        cardToTokenMap.entrySet().stream().forEach((cardTokens) -> {
            cardTokens.getValue().stream().forEach((token) -> {
                output.format("%s\t%s%n", token, cardTokens.getKey());
            });
        });
    }

    public void savePairsToXml() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tokensFileName);
            ObjectOutputStream output = xs.createObjectOutputStream(out, "tokens");
            tokenToCardMap.entrySet().stream().forEach((entry) -> {
                Pair pair = new Pair(entry.getValue(), entry.getKey());
                try {
                    output.writeObject(pair);
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

    public int getTokenizeMinimum() {
        return TOKENIZE_MIN;
    }

    public int getDetokenizeMinimum() {
        return DETOKENIZE_MIN;
    }

}
