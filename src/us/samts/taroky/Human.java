package us.samts.taroky;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Human extends Player {
    private final Scanner s;

    public Human() {
        s = new Scanner(new InputStreamReader(System.in));
        System.out.println("What is your name?");
        setName(s.nextLine());
        setHand(new ArrayList<>());
        setWinnings(new ArrayList<>());
        setChips(100);
        resetPointCards();
    }

    public Deck shuffleDeck(Deck toShuffle) {
        int temp;
        //Indicate shuffle options
        //Apply shuffling techniques
        System.out.println("How would you like to shuffle? (1 (riffle), 2 (chop), or 3 (cut))");
        temp = s.nextInt();
        s.nextLine();
        toShuffle.shuffle(temp > 0 && temp < 4 ? temp : 1);
        while (true) {
            System.out.println("Would you like to shuffle again? (0 to leave, 1/2/3 to shuffle again)");
            temp =s.nextInt();
            s.nextLine();
            if (temp == 0) {break;}
            toShuffle.shuffle(temp > 0 && temp < 4 ? temp : 1);
        }
        return toShuffle;
    }
    public int cut() {
        System.out.println("You need to cut the deck. Type 0 to cut, 1,2,3,4,6, or 12 to have the dealer deal by 1s, 2s, 3s, 4s, 6s, or 12s");
        int temp = s.nextInt();
        s.nextLine();
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
        System.out.println("Would you like to go prever? (y/n)");
        String prever = s.nextLine();
        return prever.contains("y");
    }
    public boolean fleck() {
        System.out.println("Do you want to fleck? (y/n)");
        return s.nextLine().contains("y");
    }
    public boolean pagat() {
        System.out.println("Do you want to call the I on the end? (y/n)");
        return s.nextLine().contains("y");
    }
    public boolean valat() {
        System.out.println("Do you want to call valat? (y/n)");
        return s.nextLine().contains("y");
    }
    public boolean preverTalon(Table t) {
        System.out.println("These are the showing cards: " + t.getTalon().get(0) + ", " + t.getTalon().get(1) + ", " + t.getTalon().get(2));
        System.out.println("Keep or swap? (k/s)");
        return !s.nextLine().contains("k");
    }
    public void discard() {
        while (getHand().size() > 12) {
            System.out.println("Which card would you like to discard? (number)");
            printHand();
            int temp = s.nextInt();
            s.nextLine();
            getWinnings().add(getHand().get(temp));
        }
    }
    public String determinePartner() {
        if (!hasCard("XIX")) {
            return "XIX";
        } else if (!hasCard("XVIII")){
            System.out.println("Would you like to play by yourself? (y/n)");
            if (s.nextLine().contains("y")) {
                return "XIX";
            } else {
                return "XVIII";
            }
        } else if (!hasCard("XVII")){
            System.out.println("Would you like to play by yourself? (y/n)");
            if (s.nextLine().contains("y")) {
                return "XIX";
            } else {
                return "XVII";
            }
        } else if (!hasCard("XVI")){
            System.out.println("Would you like to play by yourself? (y/n)");
            if (s.nextLine().contains("y")) {
                return "XIX";
            } else {
                return "XVI";
            }
        } else if (!hasCard("XV")){
            System.out.println("Would you like to play by yourself? (y/n)");
            if (s.nextLine().contains("y")) {
                return "XIX";
            } else {
                return "XV";
            }
        }
        return "XIX";
    }
    public Card lead() {
        System.out.println("Which card would you like to play? (number)");
        printHand();
        int t = s.nextInt();
        s.nextLine();
        return getHand().remove(t);
    }
    public Card takeTurn(Card.Suit leadingSuit) {
        sortHand(leadingSuit);
        System.out.println("Which card would you like to play? (number)");
        printHand();
        int t = s.nextInt();
        s.nextLine();
        if (t < 0 || t > getHand().size()-1) {
            System.out.println("Please choose a number from 0 - " + getHand().size());
            return takeTurn(leadingSuit);
        } else if (hasSuit(leadingSuit) && !getHand().get(t).getSuit().equals(leadingSuit)) {
            System.out.println("Please choose a " + leadingSuit);
            return takeTurn(leadingSuit);
        } else if (hasSuit(Card.Suit.TRUMP) && !getHand().get(t).getSuit().equals(Card.Suit.TRUMP)) {
            System.out.println("Please play a trump");
            return takeTurn(leadingSuit);
        }
        return getHand().remove(t);
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

