package us.samts.taroky;

import java.util.ArrayList;

public class Robot extends Player {
    public Robot() {

    }
    public Deck shuffleDeck(Deck toShuffle) {
        for (int i=0; i<(int)(Math.random()*10 + 2);i++) {
            toShuffle.shuffle((int)(Math.random()*2+1));
        }
        return toShuffle;
    }
    public int cut() {
        return (int)(Math.random()*20);
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
        return false;
    }
    public void drawTalon(int x, Table t) {
        for (int i=0;i<x;i++) {
            dealCard(t.getTalon().remove(0));
        }
    }
    public ArrayList<Card> discard() {
        sortHand();
        ArrayList<Card> toDiscard = new ArrayList<>();
        while (getHand().size() > 12) {
            getWinnings().add(getHand().get(0));
            toDiscard.add(getHand().remove(0));
        }
        return toDiscard;
    }
    public String determinePartner() {
        if (!hasCard("XIX")) {
            return "XIX";
        } else if (!hasCard("XVIII")){
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
        return getHand().get(0);
    }

    public Card takeTurn(Card.Suit leadingSuit) {
        sortHand(leadingSuit);
        return getHand().get(0);
    }
    public void sortHand() {
        ArrayList<Card> newHand = new ArrayList<>();
        for (int i=0;i<getHand().size();i++) {
            newHand.add(getHand().remove(0));
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.SPADES && newHand.get(i).getPointValue() != 5) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.CLUBS && newHand.get(i).getPointValue() != 5) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.HEARTS && newHand.get(i).getPointValue() != 5) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.DIAMONDS && newHand.get(i).getPointValue() != 5) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.TRUMP && newHand.get(i).getPointValue() != 5) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=0;i<newHand.size();i++) {
            getHand().add(newHand.remove(i));
        }
    }
    public void sortHand(Card.Suit s) {
        ArrayList<Card> newHand = new ArrayList<>();
        for (int i=0;i<getHand().size();i++) {
            newHand.add(getHand().remove(0));
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == s) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.TRUMP) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.CLUBS) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.HEARTS) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.DIAMONDS) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=newHand.size()-1;i>=0;i--) {
            if (newHand.get(i).getSuit() == Card.Suit.SPADES) {
                getHand().add(newHand.remove(i));
            }
        }
        for (int i=0;i<newHand.size();i++) {
            getHand().add(newHand.remove(i));
        }
    }
}
