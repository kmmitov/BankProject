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
public class Pair implements Comparable<Pair> {
    
    private final String cardNumber;
    
    private final String token;

    public Pair(String cardNumber, String token) {
        this.cardNumber = cardNumber;
        this.token = token;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getToken() {
        return token;
    }

    @Override
    public int compareTo(Pair o) {
        return token.compareTo(o.getToken());
    }
    
    
}
