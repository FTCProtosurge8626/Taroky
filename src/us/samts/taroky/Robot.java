package us.samts.taroky;

import java.util.ArrayList;

public class Robot extends Player {
    public Robot() {
        setName("Robot");
        setHand(new ArrayList<>());
        setWinnings(new ArrayList<>());
        setChips(100);
        resetPointCards();
    }
    public Robot(int number) {
        setName("Robot " + number);
        setHand(new ArrayList<>());
        setWinnings(new ArrayList<>());
        setChips(100);
        resetPointCards();
    }
    public Robot(String name) {
        setName(name);
        setHand(new ArrayList<>());
        setWinnings(new ArrayList<>());
        setChips(100);
        resetPointCards();
    }

    public Deck shuffleDeck(Deck toShuffle) {
        if (Table.getPrint()) {System.out.println(getName() + " shuffled the deck");}
        for (int i=0; i<(int)(Math.random()*10 + 2);i++) {
            toShuffle.shuffle((int)(Math.random()*2+1));
        }
        return toShuffle;
    }
    public int cut() {
        if (Table.getPrint()) {System.out.println(getName() + " cut the deck");}
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
        if (Table.getPrint()) {System.out.println(getName() + " drew " + x + " cards from the Talon");}
        for (int i=0;i<x;i++) {
            dealCard(t.getTalon().remove(0));
        }
    }
    public ArrayList<Card> discard() {
        sortHand();
        ArrayList<Card> toDiscard = new ArrayList<>();
        while (getHand().size() > 12) {
            getWinnings().add(getHand().get(0));
            if (getHand().get(0).getSuit()==Card.Suit.TRUMP && Table.getPrint()) {
                System.out.println(getName() + " discarded a trump card: " + getHand().get(0));
            }
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
    public boolean preverTalon(Table t) {
        return true;
    }
    public boolean fleck() {
        return false;
    }
    public Card lead() {
        if (Table.getPrint()) {System.out.println(getName() + " led the " + getHand().get(0));}
        return getHand().remove(0);
    }
    public Card takeTurn(Card.Suit leadingSuit) {
        sortHand(leadingSuit);
        if (Table.getPrint()) {System.out.println(getName() + " played the " + getHand().get(0));}
        return getHand().remove(0);
    }
    public void sortHand() {
        ArrayList<Card> newHand = new ArrayList<>(getHand());
        setHand(new ArrayList<>());
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
        getHand().addAll(newHand);//Add any missing cards
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
                getHand().add(newHand.remove(i));
            }
        }
        getHand().addAll(newHand);//Add missing cards
    }
}
