package us.samts.taroky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Table {
    private final Player[] players;
    private int dealer;
    private Player leader;
    private int leaderLocation;
    private Deck deck;
    private ArrayList<Card> talon;
    private ArrayList<Player> team1;
    private ArrayList<Player> team2;
    Scanner s;

    public Table() {
        players = new Player[4];
        players[0] = new Human();
        players[1] = new Robot("Stanley");
        players[2] = new Robot("Winston");
        players[3] = new Robot("Darcey");
        deck = new Deck();
        talon = new ArrayList<>();
        team1 = new ArrayList<>();
        team2 = new ArrayList<>();
        s = new Scanner(System.in);
        startGame();
    }
    public void startGame() {
        //Used for first round and as a future runner
        roundHandler(true);
        hand(preverCheck());
        do {
            resetTable();
            roundHandler(false);
            hand(preverCheck());
            System.out.println("Another round? (y/n)");
        } while (!s.nextLine().contains("n"));
        System.out.println("Thanks for playing! Final scores: " + players[0].getChips() + ", "+ players[1].getChips() + ", "+ players[2].getChips() + ", "+ players[3].getChips());
    }
    public void roundHandler(boolean roundOne) {
        if (roundOne) {
            dealer = (int) (Math.random() * 4);
            deck = players[dealer].shuffleDeck(deck);//Deal then iterate
            players[dealer].deal(cut(), this);
            dealer = (int) (Math.random() * 4);
            if (someoneHas("II")) {
                leader = whoHas("II");
            } else if (someoneHas("III")) {
                leader = whoHas("III");
            } else if (someoneHas("IIII")) {
                leader = whoHas("IIII");
            } else if (someoneHas("V")) {
                leader = whoHas("V");
            } else if (someoneHas("VI")) {
                leader = whoHas("VI");
            } else {
                leader = whoHas("VII");
            }
            leaderLocation = leader.equals(players[0]) ? 0 : leader.equals(players[1]) ? 1 : leader.equals(players[2]) ? 2 : 3;
        } else {
            dealer = playerOffset(dealer,1);
            leaderLocation = playerOffset(dealer,1);
            leader = players[leaderLocation];
            deck = players[dealer].shuffleDeck(deck);
            players[dealer].deal(cut(), this);
        }
        System.out.println(leader + " is Povenost");
    }
    public boolean preverCheck() {
        //Used to handle the Talon and point cards. Returns true if someone plays prever. Adds players to teams as well.
        int prever = -1;//Equal to the location in the players[] of who is going prever
        for (int i=0; i<4;i++) {
            if (players[playerOffset(dealer,i+1)].goPrever()) {
                prever = playerOffset(dealer,i+1);
                if (prever!=leaderLocation) {
                    team2.add(players[prever]); //Add prever to team 2
                    if (prever != 0 && leaderLocation != 0) {team1.add(players[0]);}//Add everyone else to team 1
                    if (prever != 1 && leaderLocation != 1) {team1.add(players[1]);}
                    if (prever != 2 && leaderLocation != 2) {team1.add(players[2]);}
                    if (prever != 3 && leaderLocation != 3) {team1.add(players[3]);}
                } else {
                    team2.add(players[playerOffset(prever,1)]);//Add everyone but povenost to team 2
                    team2.add(players[playerOffset(prever,2)]);
                    team2.add(players[playerOffset(prever,3)]);
                }
                break;
            }
        }
        team1.add(leader);//Povenost is always on team 1
        //Draw from talon
        if (prever == -1) {
            //Normal game
            leader.drawTalon(4,this);
            players[playerOffset(leaderLocation,1)].drawTalon(1,this);
            players[playerOffset(leaderLocation,2)].drawTalon(1,this);
        } else {
            if (!players[prever].preverTalon(this)) {
                ArrayList<Card> tempTalon = new ArrayList<>();
                tempTalon.add(talon.remove(0));
                tempTalon.add(talon.remove(0));
                tempTalon.add(talon.remove(0));
                if (!players[prever].preverTalon(this)) {
                    if (prever!=leaderLocation) {
                        leader.setWinnings(talon);
                        talon = new ArrayList<>();
                        talon.addAll(tempTalon);
                    }
                }
            }
            players[prever].drawTalon(3,this);
            if (leaderLocation!=prever) {leader.addWinnings(talon);} else {players[playerOffset(leaderLocation,1)].addWinnings(talon);}
            //Talon is now empty
        }
        //Discard cards
        for (int i=0;i<4;i++) {
            players[i].discard();
        }
        //Announce partner
        String partner = "";
        if (prever != -1) {
            System.out.println("Everyone is working together against " + players[prever]);
        } else {
            System.out.println("Povenost (" + leader + ") is playing with " + (partner=leader.determinePartner()) + "\n");
            for (int i=0;i<4;i++) {
                if (players[i].hasCard(partner) && !players[i].equals(leader)) {
                    team1.add(players[i]);
                } else if (!players[i].equals(leader)) {
                    team2.add(players[i]);
                }
            }
        }
        //Check for money cards
        pointCards();
        return prever > -1;
    }
    public void hand(boolean prever) {
        //Hand is used AFTER point cards and partners. It determines play and payment after play.
        for (int i = 0; i < 12; i++) {
            System.out.println("Trick " + (i+1) + ":");
            leader = trick(leader);//Go through 12 tricks
        }
        int team1Points=0;//Count one team's points
        for (Player p: team1) {
            team1Points += p.countPoints();
        }
        System.out.println("Povenost's team won " + team1Points + " points");
        if (prever) {
            boolean team1pays;
            if (team1Points > 52) {
                team1Points -= 53;
                team1Points += 10;
                team1Points *= 3;
                team1Points = (int) Math.round((double) team1Points / 10);
                team1pays = false;
            } else {
                team1Points += 10;
                team1Points *= 3;
                team1Points = (int) Math.round((double) team1Points / 10);
                team1pays = true;
            }
            if (team1pays) {
                for (Player p : team1) {
                    allPay(p, team1Points);
                }
            } else {
                for (Player p : team2) {
                    allPay(p, team1Points);
                }
            }
        } else {
            boolean team1pays;
            if (team1Points > 52) {
                team1Points -= 53;
                team1Points += 10;
                team1Points *= 2;
                team1Points = (int) Math.round((double) team1Points / 10);
                team1pays = false;
            } else {
                team1Points += 10;
                team1Points *= 2;
                team1Points = (int) Math.round((double) team1Points / 10);
                team1pays = true;
            }
            if (team1pays) {
                for (Player p : team1) {
                    allPay(p, -team1Points);
                }
            } else {
                for (Player p : team2) {
                    allPay(p, -team1Points);
                }
            }
        }
        System.out.println("Team 1: " + team1);
        System.out.println("Team 2: " + team2);
        System.out.println("Results: " + players[0] + " " + players[0].getChips() + ", "+ players[1] + " " + players[1].getChips() + ", "+ players[2] + " " + players[2].getChips() + ", " + players[3] + " " + players[3].getChips());
    }
    public void resetTable() {
        for (int i=0; i<4;i++) {
            deck.getDeck().addAll(players[i].getWinnings());
            players[i].setWinnings(new ArrayList<>());
            players[i].resetPointCards();
            players[i].setHand(new ArrayList<>());
        }
        talon = new ArrayList<>();
        team1 = new ArrayList<>();
        team2 = new ArrayList<>();
        if (!deck.deckSize()) {
            System.out.println("\nNEW DECK CONSTRUCTED\n");
            deck = new Deck();
        }
    }
    public void pointCards() {
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
                    System.out.println(temp.getName() + " has No Trumps, everyone pays 4");
                    chipsOwed += 4;
                case 1://falthrough
                case 2:
                    temp.addPointCard("2 or fewer trumps");
                    System.out.println(temp.getName() + " has 2 or less trumps, everyone pays 2");
                    chipsOwed += 2;
                    break;
                case 8://fallthrough
                case 9:
                    temp.addPointCard("Little Ones");
                    System.out.println(temp.getName() + " has Little Ones, everyone pays 2");
                    chipsOwed += 2;
                    break;
                case 10:
                case 11://fallthrough
                case 12:
                    temp.addPointCard("Big ones");
                    System.out.println(temp.getName() + " has Big Ones, everyone pays 4");
                    chipsOwed += 4;
            }
            if (fiveC >= 4) {
                if (temp.hasCard("King of Spades") && temp.hasCard("King of Clubs") && temp.hasCard("King of Hearts") && temp.hasCard("King of Diamonds")) {
                    if (fiveC > 4) {
                        temp.addPointCard("All 4 Kings+");
                        System.out.println(temp.getName() + " has All 4 Kings +, everyone pays 6");
                        chipsOwed += 6;
                    } else {
                        temp.addPointCard("All 4 Kings");
                        System.out.println(temp.getName() + " has All 4 Kings, everyone pays 4");
                        chipsOwed += 4;
                    }
                } else {
                    temp.addPointCard("4 5 counts");
                    System.out.println(temp.getName() + " has 4 5 counts, everyone pays 2");
                    chipsOwed += 2;
                }
            }
            if (temp.hasCard("I") && temp.hasCard("XXI") && temp.hasCard("Škýz")) {
                temp.addPointCard("Trull");
                System.out.println(temp.getName() + " has Trull, everyone pays 2");
                chipsOwed += 2;
            }
            allPay(temp, chipsOwed);
        }
    }
    public boolean someoneHas(String cardName) {
        return players[0].hasCard(cardName) || players[1].hasCard(cardName) || players[2].hasCard(cardName) || players[3].hasCard(cardName);
    }
    public Player whoHas(String cardName) {
        if (players[0].hasCard(cardName)) {
            return players[0];
        } else if (players[1].hasCard(cardName)) {
            return players[1];
        } else if (players[2].hasCard(cardName)) {
            return players[2];
        } else {
            return players[3];
        }
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
        ArrayList<Card> trick = new ArrayList<>();
        //First player plays a card
        trick.add(currentLeader.lead());
        leaderLocation = leader.equals(players[0]) ? 0 : leader.equals(players[1]) ? 1 : leader.equals(players[2]) ? 2 : 3;
        trick.add(players[playerOffset(leaderLocation,1)].takeTurn(trick.get(0).getSuit()));
        trick.add(players[playerOffset(leaderLocation,2)].takeTurn(trick.get(0).getSuit()));
        trick.add(players[playerOffset(leaderLocation,3)].takeTurn(trick.get(0).getSuit()));
        System.out.print("\n");
        boolean trumps = false;
        for (int i=0;i<4;i++) {
            if (trick.get(i).getSuit() == Card.Suit.TRUMP) {
                trumps = true;
                break;
            }
        }
        //If trumps are present, determine highest trump. Else, determine largest card of lead suit
        int winner = -1;
        if (trumps) {
            for (int i=22; i>=0;i--) {
                for (int j=0; j<4;j++) {
                    if (trick.get(j).getId()==i) {
                        winner = j;//Winner is the player who played the highest trump
                        break;
                    }
                }
                if (winner!=-1) {
                    break;
                }
            }
        } else {
            for (int i=8; i>=0;i--) {
                for (int j=0; j<4;j++) {
                    if ((trick.get(j).getId()-22)%8==i && trick.get(j).getSuit()==trick.get(0).getSuit()) {
                        winner = j;//Winner is the player who played the highest rank of the leading suit
                        break;
                    }
                }
                if (winner!=-1) {
                    break;
                }
            }
        }
        players[playerOffset(leaderLocation,winner)].winTrick(trick);
        System.out.println(players[playerOffset(leaderLocation,winner)] + " won the trick!\n");
        return players[playerOffset(leaderLocation,winner)];
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