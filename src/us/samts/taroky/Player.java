package us.samts.taroky;

import java.util.ArrayList;

public abstract class Player {
    private ArrayList<Card> hand;
    private ArrayList<Card> winnings;
    private int chips;
    ArrayList<String> pointCards;

    public abstract Deck shuffleDeck(Deck toShuffle);
    public abstract int cut();
    public abstract void deal(int style, Table t);
    public abstract boolean goPrever();
    public abstract void drawTalon(int x, Table t);
    public abstract ArrayList<Card> discard();
    public abstract Card lead();
    public abstract Card takeTurn(Card.Suit leadingSuit);
    public abstract String determinePartner();

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
    public void setWinnings(ArrayList<Card> newW) {
        winnings = newW;
    }
    public void setChips(int newChips) {
        chips = newChips;
    }
    public void setHand(ArrayList<Card> newH) {
        hand = newH;
    }
    public void payChips(int payment) {
        chips += payment;
    }
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

    public void winTrick(ArrayList<Card> trick) {
        winnings.addAll(trick);
    }
}
