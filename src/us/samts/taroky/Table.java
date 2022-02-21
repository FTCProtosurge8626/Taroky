package us.samts.taroky;

import java.util.ArrayList;

public abstract class Table {
    private final Player[] players;
    private int dealer;
    private Player leader;
    private Player povenost;
    private int leaderLocation;
    private Deck deck;
    private ArrayList<Card> talon;
    private ArrayList<Player> team1;
    private ArrayList<Player> team2;
    private int team1Points;
    private int team2Points;
    private final int waitTime;
    private int doublers;
    private int pDoublers;

    protected Table(int waitTime) {
        this.players = new Player[4];
        this.waitTime = waitTime;
    }

    public abstract void startGame() throws InterruptedException;
    public abstract void roundHandler(boolean roundOne) throws InterruptedException;
    public abstract boolean preverCheck() throws InterruptedException;
    public abstract void hand(boolean prever) throws InterruptedException;
    public abstract void pointCards();
    public abstract void fleck();
    public abstract Player trick(Player currentLeader) throws InterruptedException;
    public abstract int cut();

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
    public void allPay(Player p, int payment) {
        for (int i = 0; i<4;i++) {
            if (!players[i].equals(p)) {players[i].payChips(-payment);}
        }
        p.payChips(payment*3);
    }
    public void teamPay(ArrayList<Player> pay, ArrayList<Player> getPaid, int amount) {
        //USED FOR 2V2 ONLY! NOT FOR 3V1!
        for (Player p: pay) {
            p.payChips(-amount);//Everyone pays
        }
        for (Player p: getPaid) {
            p.payChips(amount);//Everyone earns
        }
    }
    public void teamPay(Player pay, ArrayList<Player> getPaid, int amount) {
        //USED FOR 2V2 ONLY! NOT FOR 3V1!
        pay.payChips(-amount*3);//One player pays
        for (Player p: getPaid) {
            p.payChips(amount);//3 players earn
        }
    }
    public int playerOffset(int p1, int offset) {
        p1 += offset;
        while (p1 < 0) {
            p1 += 4;//Bigger than -1
        }
        return p1 % 4;//Smaller than 4
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
    public ArrayList<Player> getTeam1() {
        return team1;
    }
    public ArrayList<Player> getTeam2() {
        return team2;
    }
    public int getLeaderLocation() {return leaderLocation;}
    public int getDealer() {return dealer;}
    public Player getPovenost() {return povenost;}
    public Player getLeader() {return leader;}
    public int getWaitTime() {return waitTime;}
    public int getTeam1Points() {return team1Points;}
    public int getTeam2Points() {return team2Points;}
    public int getDoublers() {return doublers;}
    public int getPDoublers() {return pDoublers;}

    public void setDeck(Deck d) {
        deck = d;
    }
    public void setTalon(ArrayList<Card> t) {
        talon = t;
    }
    public void setTeam1(ArrayList<Player> t1) {
        team1 = t1;
    }
    public void setTeam2(ArrayList<Player> t2) {
        team2 = t2;
    }
    public void setLeaderLocation(int ll) {leaderLocation = ll;}
    public void setDealer(int d) {dealer = d;}
    public void setPovenost(Player p) {povenost = p;}
    public void setLeader(Player l) {leader = l;}
    public void setTeam1Points(int t1p) {team1Points = t1p;}
    public void setTeam2Points(int t2p) {team2Points = t2p;}
    public void setDoublers(int d) {doublers = d;}
    public void setPDoublers(int p) {pDoublers = p;}
}
