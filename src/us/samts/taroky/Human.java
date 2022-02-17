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
    public void deal(int style, Table t) {
        Player[] ps = t.getPlayers();
        ArrayList<Card> d = t.getDeck().getDeck();
        for (int i=0;i<6;i++) {t.getTalon().add(d.remove(0));} //Deal talon
        while (d.size() > 0) {
            switch (style) {
                case 1:
                    for (Player p : ps) {p.dealCard(d.remove(0));}
                    break;
                case 2:
                    for (Player p : ps) {
                        for (int i=0;i<2;i++) {
                            p.dealCard(d.remove(0));
                        }
                    }
                    break;
                case 3:
                    for (Player p : ps) {
                        for (int i=0;i<3;i++) {
                            p.dealCard(d.remove(0));
                        }
                    }
                    break;
                case 4:
                    for (Player p : ps) {
                        for (int i=0;i<4;i++) {
                            p.dealCard(d.remove(0));
                        }
                    }
                    break;
                case 12:
                    for (Player p : ps) {
                        for (int i=0;i<12;i++) {
                            p.dealCard(d.remove(0));
                        }
                    }
                    break;
                default:
                    for (Player p : ps) {
                        for (int i=0;i<6;i++) {
                            p.dealCard(d.remove(0));
                        }
                    }
            }
        }
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
    public void drawTalon(int x, Table t) {
        System.out.println(getName() + " drew " + x + " cards from the Talon");
        for (int i=0;i<x;i++) {
            dealCard(t.getTalon().remove(0));
        }
    }
    public boolean preverTalon(Table t) {
        System.out.println("These are the showing cards: " + t.getTalon().get(0) + ", " + t.getTalon().get(1) + ", " + t.getTalon().get(2));
        System.out.println("Keep or swap? (k/s)");
        return s.nextLine().contains("k");
    }
    public ArrayList<Card> discard() {
        ArrayList<Card> discardPile = new ArrayList<>();
        while (getHand().size() > 12) {
            System.out.println("Which card would you like to discard? (number)");
            printHand();
            int temp = s.nextInt();
            s.nextLine();
            getWinnings().add(getHand().get(temp));
            discardPile.add(getHand().remove(temp));
        }
        return discardPile;
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
        System.out.println("Which card would you like to play? (number)");
        printHand();
        int t = s.nextInt();
        s.nextLine();
        return getHand().remove(t);
    }
    public void sortHand(Card.Suit s) {
        ArrayList<Card> newHand = new ArrayList<>();
        for (int i=0;i<getHand().size();i++) {
            newHand.add(getHand().remove(0));
        }
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

