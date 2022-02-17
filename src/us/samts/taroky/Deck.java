package us.samts.taroky;

import java.util.ArrayList;
public class Deck {
    private final ArrayList<Card> deck = new ArrayList<>();
    public Deck() {
        ArrayList<Card> standard = new ArrayList<>();
        for (int i=1;i<55;i++) {
            standard.add(new Card(i));
        }
        for (int i=0;i<54;i++) {
            deck.add(standard.remove((int)(Math.random()*standard.size())));
        }
    }
    public void shuffle(int style) {
        ArrayList<Card> temp = new ArrayList<>();
        switch (style) {
            case 1 -> { //Split in half and perfectly layer them
                for (int i = 0; i < 27; i++) {
                    temp.add(deck.remove(0));
                }
                int t = 0;
                while (temp.size() > 0) {
                    deck.add(t, temp.remove(0));
                    t += 2;
                }
                if (deck.size() != 54) {
                    throw new Error("Error: us.samts.taroky.Deck.shuffle.1 : Illegal shuffle resulted in incorrectly sized deck");
                }
            }
            case 2 -> { //Break off half then plop parts of it until it's gone
                for (int i = 0; i < 27; i++) {
                    temp.add(deck.remove(0));
                }
                while (temp.size() > 0) {
                    for (int i = (int) Math.floor(Math.random() * Math.random() * temp.size() + 1); i > 0; i--) {
                        if (i > temp.size()) {
                            i = temp.size();
                        }
                        deck.add(temp.remove(temp.size() - i));
                    }
                }
                if (deck.size() != 54) {
                    throw new Error("Error: us.samts.taroky.Deck.shuffle.2 : Illegal shuffle resulted in incorrectly sized deck");
                }
            }
            case 3 -> { //Cut
                for (int i = 0; i <= 27; i++) {
                    deck.add(deck.remove(0));
                }
                if (deck.size() != 54) {
                    throw new Error("Error: us.samts.taroky.Deck.shuffle.3 : Illegal shuffle resulted in incorrectly sized deck");
                }
            }
        }
    }
    public Card getCard(int location) {
        return deck.get(location);
    }
    public ArrayList<Card> getDeck() {
        return deck;
    }
    public boolean deckSize() {
        return deck.size()==54;
    }
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        for (Card c: deck) {
            s.append(c.toString()).append(", ");
        }
        return s.substring(0,s.length()-2) + "]";
    }
}