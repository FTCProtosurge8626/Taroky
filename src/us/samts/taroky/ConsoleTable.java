package us.samts.taroky;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleTable extends Table {
    Scanner s;
    private static boolean print;

    public ConsoleTable() {
        super(500);
        getPlayers()[0] = new Robot();
        getPlayers()[1] = new Robot("Samuel");
        getPlayers()[2] = new Robot("Daniel");
        getPlayers()[3] = new Robot("Benjamin");
        setDeck(new Deck());
        setTalon(new ArrayList<>());
        setTeam1(new ArrayList<>());
        setTeam2(new ArrayList<>());
        s = new Scanner(System.in);
        print = true;
        setDoublers(0);
        setPDoublers(0);
    }
    public void startGame() throws InterruptedException {
        //Called to start a game
        resetTable();
        roundHandler(true);
        hand(preverCheck());
    }
    public void roundHandler(boolean roundOne) throws InterruptedException {
        if (roundOne) {
            setDealer((int) (Math.random() * 4));
            setDeck(getPlayers()[getDealer()].shuffleDeck(getDeck()));
            getPlayers()[getDealer()].deal(cut(), this);
            if (someoneHas("II")) {
                setLeader(whoHas("II"));
            } else if (someoneHas("III")) {
                setLeader(whoHas("III"));
            } else if (someoneHas("IIII")) {
                setLeader(whoHas("IIII"));
            } else if (someoneHas("V")) {
                setLeader(whoHas("V"));
            } else if (someoneHas("VI")) {
                setLeader(whoHas("VI"));
            } else {
                setLeader(whoHas("VII"));
            }
            setLeaderLocation(getLeader().equals(getPlayers()[0]) ? 0 : getLeader().equals(getPlayers()[1]) ? 1 : getLeader().equals(getPlayers()[2]) ? 2 : 3);
            setDealer(playerOffset(getLeaderLocation(),-1));//So that next round the dealer will be correct
        } else {
            setDealer(playerOffset(getDealer(),1));
            setLeaderLocation(playerOffset(getDealer(),1));
            setLeader(getPlayers()[getLeaderLocation()]);
            setDeck(getPlayers()[getDealer()].shuffleDeck(getDeck()));
            getPlayers()[getDealer()].deal(cut(), this);
        }
        setPovenost(getLeader());
        Thread.sleep(getWaitTime());
        if (print) {System.out.println(getLeader() + " is Povenost");}
        Thread.sleep(getWaitTime());
    }
    public boolean preverCheck() throws InterruptedException {
        //Used to handle the Talon and point cards. Returns true if someone plays prever. Adds players to teams as well.
        int prever = -1;//Equal to the location in the players[] of who is going prever
        for (int i=0; i<4;i++) {
            if (getPlayers()[playerOffset(getDealer(),i+1)].goPrever()) {
                prever = playerOffset(getDealer(),i+1);
                if (prever!=getLeaderLocation()) {
                    getTeam2().add(getPlayers()[prever]); //Add prever to team 2
                    if (prever != 0 && getLeaderLocation() != 0) {getTeam1().add(getPlayers()[0]);}//Add everyone else to team 1
                    if (prever != 1 && getLeaderLocation() != 1) {getTeam1().add(getPlayers()[1]);}
                    if (prever != 2 && getLeaderLocation() != 2) {getTeam1().add(getPlayers()[2]);}
                    if (prever != 3 && getLeaderLocation() != 3) {getTeam1().add(getPlayers()[3]);}
                } else {
                    getTeam2().add(getPlayers()[playerOffset(prever,1)]);//Add everyone but povenost to team 2
                    getTeam2().add(getPlayers()[playerOffset(prever,2)]);
                    getTeam2().add(getPlayers()[playerOffset(prever,3)]);
                }
                break;
            }
        }//Ask each player if they'd like to go prever
        getTeam1().add(getLeader());//Povenost is always on team 1
        if (prever == -1) {
            //Normal game
            getLeader().drawTalon(4,this);
            getPlayers()[playerOffset(getLeaderLocation(),1)].drawTalon(1,this);
            getPlayers()[playerOffset(getLeaderLocation(),2)].drawTalon(1,this);
        } else {
            if (!getPlayers()[prever].preverTalon(this)) {
                ArrayList<Card> tempTalon = new ArrayList<>();
                tempTalon.add(getTalon().remove(0));
                tempTalon.add(getTalon().remove(0));
                tempTalon.add(getTalon().remove(0));
                setPDoublers(getPDoublers() + 1);//Double winnings only if prever loses
                if (!getPlayers()[prever].preverTalon(this)) {
                    if (prever!=getLeaderLocation()) {
                        getLeader().setWinnings(getTalon());
                        setTalon(new ArrayList<>());
                        getTalon().addAll(tempTalon);
                        setPDoublers(getPDoublers() + 1);//Double winnings again only if prever loses
                    }
                }
            }
            getPlayers()[prever].drawTalon(3,this);
            if (getLeaderLocation()!=prever) {getLeader().addWinnings(getTalon());} else {getPlayers()[playerOffset(getLeaderLocation(),1)].addWinnings(getTalon());}
            //Talon is now empty
        }//Talon
        Thread.sleep(getWaitTime());
        //Discard cards
        for (int i=0;i<4;i++) {
            getPlayers()[i].discard();
        }
        //Announce partner
        String partner;
        if (prever != -1) {
            if (print) {System.out.println("Everyone is working together against " + getPlayers()[prever]);}
        } else {
            //2 teams
            partner=getLeader().determinePartner();
            if (print) {System.out.println("Povenost (" + getLeader() + ") is playing with " + partner + "\n");}
            for (int i=0;i<4;i++) {
                if (getPlayers()[i].hasCard(partner) && !getPlayers()[i].equals(getLeader())) {
                    getTeam1().add(getPlayers()[i]);
                } else if (!getPlayers()[i].equals(getLeader())) {
                    getTeam2().add(getPlayers()[i]);
                }
            }
        }
        Thread.sleep(getWaitTime());
        //Check for money cards
        pointCards();
        fleck();
        return prever > -1;
    }
    public void hand(boolean prever) throws InterruptedException {
        //Hand is used AFTER point cards and partners. It determines play and payment after play.
        for (int i = 0; i < 12; i++) {
            if (print) {System.out.println("Trick " + (i+1) + ":");}
            setLeader(trick(getLeader()));//Go through 12 tricks
        }
        Thread.sleep(getWaitTime());
        setTeam1Points(0);//Count one team's points
        for (Player p: getTeam1()) {
            setTeam1Points(getTeam1Points() + p.countPoints());
        }
        if (print) {System.out.println("Povenost's team won " + getTeam1Points() + " points");}
        if (prever) {
            boolean team1pays;
            if (getTeam1Points() > 53) {
                setTeam1Points(getTeam1Points() - 53);
                setTeam1Points(getTeam1Points() + 10);
                setTeam1Points(getTeam1Points() *  3);
                setTeam1Points( (int) Math.round((double) getTeam1Points() / 10));
                team1pays = true;
            } else {
                setTeam1Points( 53 - getTeam1Points());
                setTeam1Points(getTeam1Points() + 10);
                setTeam1Points(getTeam1Points() *  3);
                setTeam1Points( (int) Math.round((double) getTeam1Points() / 10));
                team1pays = false;
            }
            setTeam1Points(getTeam1Points() *  (int) Math.pow(2,getDoublers()));
            if (getTeam1().size() == 1) { //Pavenost is prever
                if (team1pays) {
                    setTeam1Points((int) (getTeam1Points() *  Math.pow(2,getPDoublers())));//Double it a lot if prever loses
                    teamPay(getPovenost(), getTeam2(), getTeam1Points());//Pavenost/prever pays
                    if (print) {System.out.println("Team 2 pays: " + getTeam1Points() + " chips");}
                } else {
                    teamPay(getPovenost(), getTeam2(), -getTeam1Points());//Povenost gets paid
                    if (print) {System.out.println("Team 1pays: " + getTeam1Points() + " chips");}
                }
            } else {//Someone else is prever
                if (team1pays) {
                    setTeam1Points((int) (getTeam1Points() *  Math.pow(2,getPDoublers())));//Double it a lot if prever loses
                    teamPay(getTeam2().get(0), getTeam1(), getTeam1Points());//Prever pays
                    if (print) {System.out.println("Team 2 pays: " + getTeam1Points() + " chips");}
                } else {
                    teamPay(getTeam2().get(0), getTeam1(), -getTeam1Points());//Prever gets paid
                    if (print) {System.out.println("Team 1pays: " + getTeam1Points() + " chips");}
                }
            }
        } else {
            boolean team1pays;
            if (getTeam1Points() > 53) {
                setTeam1Points(getTeam1Points() -  53);
                setTeam1Points(getTeam1Points() + 10);
                setTeam1Points(getTeam1Points() *  2);
                setTeam1Points( (int) Math.round((double) getTeam1Points() / 10));
                team1pays = true;
            } else {
                setTeam1Points( 53 - getTeam1Points());
                setTeam1Points(getTeam1Points() + 10);
                setTeam1Points(getTeam1Points() *  2);
                setTeam1Points( (int) Math.round((double) getTeam1Points() / 10));
                team1pays = false;
            }
            setTeam1Points(getTeam1Points() *  (int) Math.pow(2,getDoublers()));
            if (getTeam1().size() == 2) {
                if (team1pays) {
                    teamPay(getTeam2(), getTeam1(), getTeam1Points());
                    if (print) {System.out.println("Team 2 pays: " + getTeam1Points() + " chips");}
                } else {
                    teamPay(getTeam1(), getTeam2(), getTeam1Points());
                    if (print) {System.out.println("Team 1 pays: " + getTeam1Points() + " chips");}
                }
            } else if (getTeam1().size() == 1) {//Pavenost is alone
                if (team1pays) {
                    teamPay(getPovenost(), getTeam2(), getTeam1Points());
                    if (print) {System.out.println("Team 2 pays: " + getTeam1Points() + " chips");}
                } else {
                    teamPay(getPovenost(), getTeam2(), -getTeam1Points());
                    if (print) {System.out.println("Team 1pays: " + getTeam1Points() + " chips");}
                }
            }
        }
        if (print) {
            System.out.println("Team 1: " + getTeam1());
            System.out.println("Team 2: " + getTeam2());
            System.out.println("Results: " + getPlayers()[0] + " " + getPlayers()[0].getChips() + ", "+ getPlayers()[1] + " " + getPlayers()[1].getChips() + ", "+ getPlayers()[2] + " " + getPlayers()[2].getChips() + ", " + getPlayers()[3] + " " + getPlayers()[3].getChips());
        }
        if (getTeam1().size() + getTeam2().size() != 4) {
            System.out.println("Less than 4 players!");
        }
    }
    public void pointCards() {
        for (int i=0;i<4;i++) {
            //All 4 players, starting with Povenost

            int chipsOwed = 0;
            Player temp = getPlayers()[playerOffset(getDealer(),i+1)];
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
                    if (print) {System.out.println(temp.getName() + " has No Trumps, everyone pays 4");}
                    chipsOwed += 4;
                case 1://fallthrough
                case 2:
                    temp.addPointCard("2 or fewer trumps");
                    if (print) {System.out.println(temp.getName() + " has 2 or less trumps, everyone pays 2");}
                    chipsOwed += 2;
                    break;
                case 8://fallthrough
                case 9:
                    temp.addPointCard("Little Ones");
                    if (print) {System.out.println(temp.getName() + " has Little Ones, everyone pays 2");}
                    chipsOwed += 2;
                    break;
                case 10:
                case 11://fallthrough
                case 12:
                    temp.addPointCard("Big ones");
                    if (print) {System.out.println(temp.getName() + " has Big Ones, everyone pays 4");}
                    chipsOwed += 4;
            }
            if (fiveC >= 4) {
                if (temp.hasCard("King of Spades") && temp.hasCard("King of Clubs") && temp.hasCard("King of Hearts") && temp.hasCard("King of Diamonds")) {
                    if (fiveC > 4) {
                        temp.addPointCard("All 4 Kings+");
                        if (print) {System.out.println(temp.getName() + " has All 4 Kings +, everyone pays 6");}
                        chipsOwed += 6;
                    } else {
                        temp.addPointCard("All 4 Kings");
                        if (print) {System.out.println(temp.getName() + " has All 4 Kings, everyone pays 4");}
                        chipsOwed += 4;
                    }
                } else {
                    temp.addPointCard("4 5 counts");
                    if (print) {System.out.println(temp.getName() + " has 4 5 counts, everyone pays 2");}
                    chipsOwed += 2;
                }
            }
            if (temp.hasCard("I") && temp.hasCard("XXI") && temp.hasCard("Škýz")) {
                temp.addPointCard("Trull");
                if (print) {System.out.println(temp.getName() + " has Trull, everyone pays 2");}
                chipsOwed += 2;
            }
            allPay(temp, chipsOwed);
        }
    }
    public void fleck() {
        for (Player t2: getTeam2()) {
            if (t2.fleck()) {
                setDoublers(getDoublers()+1);
                for (Player t1: getTeam1()) {
                    if (t1.fleck()) {
                        setDoublers(getDoublers()+1);
                        for (Player t2t2 : getTeam2()) {
                            if (t2t2.fleck()) {
                                setDoublers(getDoublers()+1);
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
    public int cut() {
        int t = getPlayers()[playerOffset(getDealer(),-1)].cut();
        if (t==0) {
            getDeck().shuffle(3);//cut
            return 6;//Deal by 6s
        } else {
            return t;
        }
    }
    public Player trick(Player currentLeader) throws InterruptedException {
        ArrayList<Card> trick = new ArrayList<>();
        //First player plays a card
        trick.add(currentLeader.lead());
        Thread.sleep(getWaitTime()* 3L);
        setLeaderLocation(getLeader().equals(getPlayers()[0]) ? 0 : getLeader().equals(getPlayers()[1]) ? 1 : getLeader().equals(getPlayers()[2]) ? 2 : 3);
        trick.add(getPlayers()[playerOffset(getLeaderLocation(),1)].takeTurn(trick.get(0).getSuit()));
        Thread.sleep(getWaitTime()* 3L);
        trick.add(getPlayers()[playerOffset(getLeaderLocation(),2)].takeTurn(trick.get(0).getSuit()));
        Thread.sleep(getWaitTime()* 3L);
        trick.add(getPlayers()[playerOffset(getLeaderLocation(),3)].takeTurn(trick.get(0).getSuit()));
        Thread.sleep(getWaitTime()* 3L);
        if (print) {System.out.print("\n");}
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
        getPlayers()[playerOffset(getLeaderLocation(),winner)].winTrick(trick);
        if (print) {System.out.println(getPlayers()[playerOffset(getLeaderLocation(),winner)] + " won the trick!\n");}
        Thread.sleep(getWaitTime());
        return getPlayers()[playerOffset(getLeaderLocation(),winner)];
    }
    public static boolean getPrint() {
        return print;
    }
}