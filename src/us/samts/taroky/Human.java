package us.samts.taroky;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Human extends Player {
    private Table t;

    public Human(String name, Table table) {
        setName(name);
        setHand(new ArrayList<>());
        setWinnings(new ArrayList<>());
        setChips(100);
        resetPointCards();
        t = table;
    }

    public Deck shuffleDeck(Deck toShuffle) {
        int temp;
        //Indicate shuffle options
        //Apply shuffling techniques
        temp = t.getInputInt("How would you like to shuffle? (1 (riffle), 2 (chop), or 3 (cut))");
        toShuffle.shuffle(temp > 0 && temp < 4 ? temp : 1);
        while (true) {
            t.incrementShuffleCount();
            temp = t.getInputInt("Would you like to shuffle again? (0 to leave, 1/2/3 to shuffle again)");
            if (temp == 0) {break;}
            toShuffle.shuffle(temp > 0 && temp < 4 ? temp : 1);
        }
        return toShuffle;
    }
    public int cut() {
        int temp = t.getInputInt("You need to cut the deck. Type 0 to cut, 1,2,3,4,6, or 12 to have the dealer deal by 1s, 2s, 3s, 4s, 6s, or 12s");
        return switch (temp) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            case 6 -> 6;
            case 12 -> 12;
            case 345 -> 345;
            default -> 0;
        };
    }
    public boolean goPrever() {
        sortHand(Card.Suit.TRUMP);
        sortHand(Card.Suit.SPADES);
        sortHand(Card.Suit.HEARTS);
        sortHand(Card.Suit.CLUBS);
        printHand();
        return t.getInputBoolean("Would you like to go prever?");
    }
    public boolean fleck() {
        return t.getInputBoolean("Do you want to fleck?");
    }
    public boolean pagat() {
        return t.getInputBoolean("Do you want to call the I on the end? ");
    }
    public boolean valat() {
        return t.getInputBoolean("Do you want to call valat? ");
    }
    public boolean preverTalon(Table t) {
        return t.getInputBoolean("These are the showing cards: " + t.getTalon().get(0) + ", " + t.getTalon().get(1) + ", " + t.getTalon().get(2) + " Do you want to swap? ");//keep is false, swap is true
    }
    public void discard() {
        while (getHand().size() > 12) {
            printHand();
            int temp = t.getInputInt("Which card would you like to discard? (number)");
            //Add exceptions for trump and kings
            getWinnings().add(getHand().get(temp));
            getHand().remove(temp);
        }
    }
    public String determinePartner() {
        if (!hasCard("XIX")) {
            return "XIX";
        } else {
            if (t.getInputBoolean("Would you like to call the XIX and play alone? ")) {
                return "XIX";
            }
        }
        if (!hasCard("XVIII")){
            return "XVIII";
        } else if (!hasCard("XVII")){
            return "XVII";
        } else if (!hasCard("XVI")){
            return "XVI";
        } else if (!hasCard("XV")){
            return "XV";
        }
        return "XIX";
    }
    public Card lead() {
        printHand();
        int temp = t.getInputInt("Which card would you like to play? (number)");
        return getHand().remove(temp);
    }
    public Card takeTurn(Card.Suit leadingSuit) {
        sortHand(leadingSuit);
        printHand();
        int temp = t.getInputInt("Which card would you like to play? (number)");
        if (temp < 0 || temp > getHand().size()-1) {
            System.out.println("Please choose a number from 0 - " + (getHand().size()-1));
            return takeTurn(leadingSuit);
        } else if (hasSuit(leadingSuit) && !getHand().get(temp).getSuit().equals(leadingSuit)) {
            System.out.println("Please choose a " + leadingSuit);
            return takeTurn(leadingSuit);
        } else if (hasSuit(Card.Suit.TRUMP) && !getHand().get(temp).getSuit().equals(Card.Suit.TRUMP) && !getHand().get(temp).getSuit().equals(leadingSuit)) {
            System.out.println("Please play a trump");
            return takeTurn(leadingSuit);
        }
        return getHand().remove(temp);
    }
    public void sortHand(Card.Suit s) {
        ArrayList<Card> newHand = new ArrayList<>(getHand());
        setHand(new ArrayList<>());
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == s) {
                getHand().add(0,newHand.remove(i));
            }
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.TRUMP) {
                getHand().add(0,newHand.remove(i));
            }
        }
        getHand().addAll(newHand);//Add missing cards
    }
}

