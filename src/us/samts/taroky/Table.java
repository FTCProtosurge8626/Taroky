package us.samts.taroky;

import java.util.ArrayList;
import java.util.Scanner;

public class Table {
    private final Player[] players;
    private int dealer;
    private Player leader;
    private Deck deck;
    private final ArrayList<Card> talon;
    private ArrayList<Player> team1;
    private ArrayList<Player> team2;
    Scanner s;

    public Table() {
        players = new Player[4];
        players[0] = new Human();
        players[1] = new Robot();
        players[2] = new Robot();
        players[3] = new Robot();
        deck = new Deck();
        talon = new ArrayList<>();
        team1 = new ArrayList<>();
        team2 = new ArrayList<>();
        s = new Scanner(System.in);
        startGame();
    }
    public void startGame() {
        //Choose dealer randomly
        dealer = (int) (Math.random() * 4);
        leader = players[dealer];//Change to whoever has the II
        hand();
    }
    public void hand() {
        //Deal cards
        deck = players[dealer].shuffleDeck(deck);
        players[dealer].deal(cut(), this);
        leader = players[(dealer+1)%4];
        team1.add(players[playerOffset(dealer,1)]);//Add povenost
        team2.add(players[dealer]);
        team2.add(players[playerOffset(dealer,2)]);
        team2.add(players[playerOffset(dealer,3)]);//Other players
        //Check for prever
        int prever = -1;
        for (int i=0; i<4;i++) {
            if (players[playerOffset(dealer,i+1)].goPrever()) {
                prever = playerOffset(dealer,i+1);
                if (playerOffset(dealer,i+1) != playerOffset(dealer,1)) {
                    team1.add(team2.remove(Math.min(i, 2)));//FIX THIS LATER
                    team2.add(team1.remove(0));
                }
                break;
            }
        }
        //Draw from talon
        if (prever == -1) {
            //Normal game
            players[playerOffset(dealer,1)].drawTalon(4,this);
            players[playerOffset(dealer,1)].drawTalon(1,this);
            players[playerOffset(dealer,1)].drawTalon(1,this);
        } else {
            players[prever].drawTalon(3,this);
        }
        //Discard cards
        for (int i=0;i<4;i++) {
            players[i].discard();
        }
        //Announce partner
        String partner;
        System.out.println("Povenost is playing with " + (partner=leader.determinePartner()));
        for (int i=0;i<team2.size();i++) {
            if (team2.get(i).hasCard(partner)) {
                team1.add(team2.remove(i));
                break;
            }
        }
        //Check for money cards
        for (int i=0;i<4;i++) {
            //All 4 players, starting with Povenost

            int chipsOwed = 0;
            Player temp = players[playerOffset(dealer,i+1)];
            int trumps = 0;
            int fiveC = 0;
            for (Card c : temp.getHand()) {
                if (c.getSuit() == Card.Suit.TRUMP) {
                    trumps++;
                }
                if (c.getPointValue() == 5) {
                    fiveC++;
                }
            }
            switch (trumps) {
                case 0:
                    temp.addPointCard("No trumps");
                    chipsOwed += 4;
                case 1://falthrough
                case 2:
                    temp.addPointCard("2 or fewer trumps");
                    chipsOwed += 2;
                    break;
                case 8://fallthrough
                case 9:
                    temp.addPointCard("Little Ones");
                    chipsOwed += 2;
                    break;
                case 10:
                case 11://fallthrough
                case 12:
                    temp.addPointCard("Big ones");
                    chipsOwed += 4;
            }
            if (fiveC >= 4) {
                if (temp.hasCard("King of Spades") && temp.hasCard("King of Clubs") && temp.hasCard("King of Hearts") && temp.hasCard("King of Diamonds")) {
                    if (fiveC > 4) {
                        temp.addPointCard("All 4 Kings+");
                        chipsOwed += 6;
                    } else {
                        temp.addPointCard("All 4 Kings");
                        chipsOwed += 4;
                    }
                } else {
                    temp.addPointCard("4 5 counts");
                    chipsOwed += 2;
                }
            }
            if (temp.hasCard("I") && temp.hasCard("XXI") && temp.hasCard("Škýz")) {
                temp.addPointCard("Trull");
                chipsOwed += 2;
            }
            allPay(temp, chipsOwed);
        }
        for (int i = 0; i < 12; i++) {
            leader = trick(leader);//Go through 12 tricks
        }
        int team1Points=0;
        for (Player p: team1) {
            team1Points += p.countPoints();
        }
        for (Player p: team1) {
            if (team1Points > 53) {
                allPay(p, (int) Math.round(((double)team1Points-43)*2)/10);
            } else {
                allPay(p, (int) Math.round((Math.abs(53-team1Points)+10)*2)/10);
            }
        }
        System.out.println("Results: " + players[0].getChips() + ", "+ players[1].getChips() + ", "+ players[2].getChips() + ", "+ players[3].getChips());
        System.out.println("Would you like to play another round?");
        if (s.nextLine().contains("y")) {
            hand();
        }
        //Play another round?
    }
    public int cut() {
        int t = players[playerOffset(dealer,-1)].cut();
        if (t==0) {
            deck.shuffle(3);//cut
            return 6;//Deal by 6s
        } else {
            return t;
        }
    }
    public void allPay(Player p, int payment) {
        for (int i = 0; i<4;i++) {
            players[i].payChips(-payment);
        }
        p.payChips(payment*4);
    }
    public Player trick(Player currentLeader) {
        //First player plays a card
        //Second player follows suit if possible, plays trump if not, sluffs if they can't
        //Third player " "
        //Fourth player " "
        //Determine winner based on suit
        //Points go to their winnings
        //Return the winner of the trick
        return currentLeader;//FIX THIS LATER
    }
    public int playerOffset(int p1, int offset) {
        p1 = p1 - offset;
        while (p1 < 0) {
            p1 += 4;
        }
        return p1 % 4;
    }
    public Deck getDeck() {
        return deck;
    }
    public Player[] getPlayers() {
        return players;
    }
    public ArrayList<Card> getTalon() {
        return talon;
    }
}