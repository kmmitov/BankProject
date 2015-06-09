/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import data.BankData;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Kris
 */
public class Tokenizer {

    private final BankData model;

    private final Random random;
    
    private final static int NUMBER_SIZE = 16;

    private final ArrayList<Integer> digits;

    public Tokenizer(BankData model) {
        this.model = model;
        random = new Random();
        digits = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            digits.add(i);
        }
    }

    public String tokenize(String cardNumber) {
        String result = "";
        char[] token = new char[cardNumber.length()];
        if (!validateCardNumber(cardNumber)) {
            return null;
        }
        
        int[] firstOptions = {1, 2, 7, 8, 9};
        int sum = 0;
        do {
            sum = 0;
            int choice = random.nextInt(firstOptions.length);
            sum += firstOptions[choice];
            token[0] = toChar(firstOptions[choice]);
            for (int index = 1; index < NUMBER_SIZE - 4; index++) {
                int excluded = fromChar(cardNumber.charAt(index));
                digits.remove(excluded);
                choice = random.nextInt(digits.size());
                sum += digits.get(choice);
                token[index] = toChar(digits.get(choice));
                digits.add(excluded);
            }
            for (int index = NUMBER_SIZE - 4; index < NUMBER_SIZE; index++) {
                token[index] = cardNumber.charAt(index);
            }
            result = new String(token);
        } while (model.isRegistered(result)|| sum % 10 == 0);
      

        return result;

    }

    public String detokenize(String token) {
        if (!model.isRegistered(token)) {
            return null;
        }
        return model.findCard(token);
    }

    private boolean validateCardNumber(String cardNumber) {
        return cardNumber.matches("3\\d{15}|4\\d{15}|5\\d{15}"
                + "|6\\d{15}") && isLuhn(cardNumber);
    }
    
    private boolean isLuhn(String cardNumber) {
        if(cardNumber.length() != NUMBER_SIZE)
            return false;
        int[] cardNumberDigits = new int[NUMBER_SIZE];
        for(int i=0; i < NUMBER_SIZE; i++) {
            cardNumberDigits[i] = fromChar(cardNumber.charAt(i));
        }
        for(int i=NUMBER_SIZE - 2; i >= 0; i-=2) {
            cardNumberDigits[i] = 2*cardNumberDigits[i];
            if(cardNumberDigits[i] > 10) {
                cardNumberDigits[i] = cardNumberDigits[i]/10 + cardNumberDigits[i]%10;
            }
        }
        int sum = 0;
        for(int i=0; i < NUMBER_SIZE; i++)
            sum += cardNumberDigits[i];
        return sum % 10 == 0;
    }

    private int fromChar(char digit) {
        return digit - '0';
    }

    private char toChar(int choice) {
        return (char) (choice + '0');
    }
    

}   
