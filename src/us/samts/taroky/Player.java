package us.samts.taroky;

import java.util.ArrayList;

public abstract class Player {
    private ArrayList<Card> hand;
    private ArrayList<Card> winnings;
    private int chips;
    private ArrayList<String> pointCards;
    private String name;
    private int chipModifier;

    public abstract Deck shuffleDeck(Deck toShuffle);
    public abstract int cut();
    public abstract void deal(int style, Table t);
    public abstract boolean goPrever();
    public abstract void drawTalon(int x, Table t);
    public abstract ArrayList<Card> discard();
    public abstract Card lead();
    public abstract Card takeTurn(Card.Suit leadingSuit);
    public abstract String determinePartner();
    public abstract boolean preverTalon(Table t);
    public abstract boolean fleck();
    public abstract boolean pagat();

    public int countPoints() {
        int sum = 0;
        for (Card c: winnings) {
            sum += c.getPointValue();
        }
        return sum;
    }
    public void dealCard(Card c) {
        hand.add(c);
    }
    public void printHand() {
        for (int i=0;i<hand.size();i++) {
            System.out.print(i + " " + hand.get(i) + ", ");
        }
        System.out.println("");
    }
    public ArrayList<Card> getWinnings() {
        return winnings;
    }
    public int getChips() {
        return chips;
    }
    public ArrayList<Card> getHand() {
        return hand;
    }
    public String getName() {return name;}
    public void setWinnings(ArrayList<Card> newW) {
        winnings = newW;
    }
    public void addWinnings(ArrayList<Card> toAdd) {winnings.addAll(toAdd);}
    public void setChips(int newChips) {
        chips = newChips;
    }
    public void setHand(ArrayList<Card> newH) {
        hand = newH;
    }
    public void payChips(int payment) {
        chips += payment;
    }
    public void setName(String newName) {name=newName;}
    public void resetPointCards() {
        pointCards = new ArrayList<>();
    }
    public void addPointCard(String pointC) {
        pointCards.add(pointC);
    }
    public boolean hasCard(String cardName) {
        for (Card c : hand) {
            if (c.getName().equals(cardName)) {
                return true;
            }
        }
        return false;
    }
    public boolean hasSuit(Card.Suit s) {
        for (Card c : getHand()) {
            if (c.getSuit().equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void winTrick(ArrayList<Card> trick) {
        winnings.addAll(trick);
    }
    public String toString() {
        return name;
    }
    public String info() {
        return name + ":\n" + hand +"\nchips: " + chips + "\nDiscard pile: " + winnings + "\n" + pointCards + "\n";
    }
}
