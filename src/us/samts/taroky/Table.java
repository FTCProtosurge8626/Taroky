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
    private ArrayList<Player> trickWinners;
    private int team1Points;
    private final int waitTime;
    private int doublers;
    private int pDoublers;
    private int IOTE;
    private boolean wonPagat;
    private boolean lostPagat;
    private int pagatTeam;
    private boolean valat;
    private boolean natValat;
    private int valatTeam;
    private int roundNumber;

    protected Table(int waitTime) {
        this.players = new Player[4];
        this.waitTime = waitTime;
        roundNumber = 0;
        deck = new Deck();
    }

    public abstract void message(String message);

    public void startGame() throws InterruptedException {
        //Called to start a game
        resetTable();
        roundNumber++;
        roundHandler(true);
        hand(preverCheck());
        while (anotherHand()) {
            resetTable();
            roundNumber++;
            roundHandler(false);
            hand(preverCheck());
        }
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
        message(getLeader() + " is Povenost");
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
            if (getPlayers()[prever].preverTalon(this)) {
                ArrayList<Card> tempTalon = new ArrayList<>();
                tempTalon.add(getTalon().remove(0));
                tempTalon.add(getTalon().remove(0));
                tempTalon.add(getTalon().remove(0));
                setPDoublers(getPDoublers() + 1);//Double winnings only if prever loses
                if (getPlayers()[prever].preverTalon(this)) {
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
            message("Everyone is working together against " + getPlayers()[prever]);
        } else {
            //2 teams
            partner=getLeader().determinePartner();
            message("Povenost (" + getLeader() + ") is playing with " + partner + "\n");
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
        valat();

        iOnTheEnd();//Ask about I on the End
        fleck();//Ask if any player wants to double the price of play
        return prever > -1;
    }
    public void hand(boolean prever) throws InterruptedException {
        //Hand is used AFTER point cards and partners. It determines play and payment after play.
        for (int i = 0; i < 12; i++) {
            message("Trick " + (i+1) + ":");
            setLeader(trick(getLeader(),i));//Go through 12 tricks
        }
        Thread.sleep(getWaitTime());
        boolean team1wontrick = false;
        boolean team2wontrick = false;

        for (int i=0;i<12;i++) {
            //Checks every winner of every trick
            if (!team1wontrick)
                for (Player t1 : team1) {
                    if (trickWinners.get(i) == t1) {
                        team1wontrick = true;
                        break;
                    }
                }
            if (!team2wontrick)
                for (Player t2 : team2) {
                    if (trickWinners.get(i) == t2) {
                        team2wontrick = true;
                        break;
                    }
                }
            if (team1wontrick && team2wontrick)
                break;
        }
        //If either team got 0 wins, set natural valat to true
        if (!(team1wontrick && team2wontrick))
            natValat = true;
        if (!valat && !natValat) {
            setTeam1Points(0);//Count one team's points
            for (Player p: getTeam1()) {
                setTeam1Points(getTeam1Points() + p.countPoints());
            }
            message("Povenost's team won " + getTeam1Points() + " points");
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
                        message("Team 2 pays: " + getTeam1Points() + " chips");
                    } else {
                        teamPay(getPovenost(), getTeam2(), -getTeam1Points());//Povenost gets paid
                        message("Team 1pays: " + getTeam1Points() + " chips");
                    }
                } else {//Someone else is prever
                    if (team1pays) {
                        setTeam1Points((int) (getTeam1Points() *  Math.pow(2,getPDoublers())));//Double it a lot if prever loses
                        teamPay(getTeam2().get(0), getTeam1(), getTeam1Points());//Prever pays
                        message("Team 2 pays: " + getTeam1Points() + " chips");
                    } else {
                        teamPay(getTeam2().get(0), getTeam1(), -getTeam1Points());//Prever gets paid
                        message("Team 1pays: " + getTeam1Points() + " chips");
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
                if (getLostPagat() || getWonPagat()) {
                    if (getPagatTeam()==0 && getIOTE() != -1) {
                        for (Player p : getTeam1()) {
                            if (p == getPlayers()[getIOTE()]) {
                                //Found pagat caller, now find which team s/he is on
                                for (Player t1 : getTeam1()) {
                                    if (p==t1) {
                                        setPagatTeam(1);
                                    }
                                }
                                for (Player t2 : getTeam2()) {
                                    if (p==t2) {
                                        setPagatTeam(2);
                                    }
                                }
                            }
                        }
                    } //Set pagatTeam to whoever called the pagat
                    //Pagat was either called or played on the last trick
                    int pChips = 2;
                    if (getIOTE() != -1) {
                        pChips = 4;//Double bounty if called
                    }
                    if (getIOTE() != -1 && getWonPagat()) {
                        //Pagat was won
                        if (getPagatTeam()==1) {
                            //Team 1 hosts pagat
                            if (getTeam1().size()==1) {
                                //1 player gets chips from all others
                                teamPay(getPovenost(), getTeam2(), pChips);
                                message("Team 1 gets " + pChips + " chips for pagat");
                            } else {
                                //2 players on team 1
                                teamPay(getTeam1(), getTeam2(), 4);
                                message("Team 1 gets " + pChips + "  for pagat");
                            }
                        } else {
                            //Team 2 hosts pagat
                            if (getTeam1().size()==1) {
                                //1 player pays chips to all others
                                teamPay(getPovenost(), getTeam2(), -pChips);
                                message("Team 1 pays: " + pChips + " chips for pagat");
                            } else {
                                //2 players on team 1, they get paid by team 2
                                teamPay(getTeam2(), getTeam1(), pChips);
                                message("Team 1 pays " + pChips + " chips for pagat");
                            }
                        }
                    } else if (getLostPagat()) {
                        //Pagat was lost
                        if (getPagatTeam()==1) {
                            //Team 1 hosts pagat
                            if (getTeam1().size()==1) {
                                //1 player gets chips from all others
                                teamPay(getPovenost(), getTeam2(), -pChips);
                                message("Team 1 pays " + pChips + " chips for pagat");
                            } else {
                                //2 players on team 1
                                teamPay(getTeam1(), getTeam2(), -pChips);
                                message("Team 1 pays " + pChips + "  for pagat");
                            }
                        } else {
                            //Team 2 hosts pagat
                            if (getTeam1().size()==1) {
                                //1 player pays chips to all others
                                teamPay(getPovenost(), getTeam2(), pChips);
                                message("Team 1 gets: " + pChips + " chips for pagat");
                            } else {
                                //2 players on team 1, they get paid by team 2
                                teamPay(getTeam1(), getTeam2(), pChips);
                                message("Team 1 gets " + pChips + " chips for pagat");
                            }
                        }
                    }
                }
                if (getTeam1().size() == 2) {
                    if (team1pays) {
                        teamPay(getTeam2(), getTeam1(), getTeam1Points());
                        message("Team 2 pays: " + getTeam1Points() + " chips");
                    } else {
                        teamPay(getTeam1(), getTeam2(), getTeam1Points());
                        message("Team 1 pays: " + getTeam1Points() + " chips");
                    }
                } else if (getTeam1().size() == 1) {//Pavenost is alone
                    if (team1pays) {
                        teamPay(getPovenost(), getTeam2(), getTeam1Points());
                        message("Team 2 pays: " + getTeam1Points() + " chips");
                    } else {
                        teamPay(getPovenost(), getTeam2(), -getTeam1Points());
                        message("Team 1 pays: " + getTeam1Points() + " chips");
                    }
                }
            }
        } else {
            //Someone valat'd
            int valatChips = 20;
            if (prever)
                valatChips = 30;
            if (valat) {
                valatChips *= 2;
                if (team1wontrick && valatTeam == 1 && !team2wontrick) {
                    if (getTeam1().size() == 2) {
                        //Both teams are 2 players, team 2 pays team 1 40 chips
                        teamPay(getTeam2(), getTeam1(), valatChips);
                        message("Team 2 pays " + valatChips + " chips. Team 1 valat'd them");
                    } else if (getTeam1().size() == 1) {
                        //Pavenost is alone, s/he valat'd them
                        teamPay(getPovenost(), getTeam2(), valatChips);
                        message("Team 2 pays " + valatChips + " chips, Pavenost valat'd you all");
                    } else {
                        //Team 2 is alone
                        teamPay(getTeam2().get(0), getTeam1(), -valatChips);
                        message("Team 2 pays " + valatChips + " chips. Team 1 valat'd you");
                    }
                } else if (team2wontrick && valatTeam == 2 && !team1wontrick) {
                    if (getTeam1().size() == 2) {
                        teamPay(getTeam1(), getTeam2(), valatChips);
                        message("Team 1 pays " + valatChips + " chips. Team 2 valat'd them");
                    } else if (getTeam1().size() == 1) {
                        //Pavenost is alone, s/he valat'd them
                        teamPay(getPovenost(), getTeam2(), -valatChips);
                        message("Team 1 pays " + valatChips + " chips, Pavenost got wrecked");
                    } else {
                        //Team 2 is alone
                        teamPay(getTeam2().get(0), getTeam1(), valatChips);
                        message("Team 1 pays " + valatChips + " chips. Team 2 valat'd you");
                    }
                } else {
                    //Valat team lost :/
                    if (valatTeam == 1) {
                        //Pavenost lost
                        if (getTeam1().size() == 2) {
                            //Both teams are 2 players, team 1 pays team 2 40 chips
                            teamPay(getTeam1(), getTeam2(), valatChips);
                            message("Team 1 pays " + valatChips + " chips. They lost a called valat");
                        } else if (getTeam1().size() == 1) {
                            //Pavenost is alone, s/he valat'd them
                            teamPay(getPovenost(), getTeam2(), -valatChips);
                            message("Pavenost pays " + valatChips + " chips. Pavenost was a bit prideful in that valata call");
                        } else {
                            //Team 2 is alone
                            teamPay(getTeam2().get(0), getTeam1(), valatChips);
                            message("Team 1 pays " + valatChips + " chips. Team 2 wrecked your valat");
                        }
                    } else {
                        //Other team lost
                        if (getTeam1().size() == 2) {
                            //Both teams are 2 players, team 2 pays team 1 40 chips
                            teamPay(getTeam2(), getTeam1(), valatChips);
                            message("Team 2 pays " + valatChips + " chips. They lost a called valat");
                        } else if (getTeam1().size() == 1) {
                            //Pavenost is alone, s/he valat'd them
                            teamPay(getPovenost(), getTeam2(), valatChips);
                            message("Team 2 pays " + valatChips + " chips. Pavenost stopped your valat. Also, YOU CALLED VALAT AND PAVENOST CALLED HIMSELF ON THE XIX, HOW DUMB ARE YOU???");
                        } else {
                            //Team 2 is alone
                            teamPay(getTeam2().get(0), getTeam1(), -valatChips);
                            message("Team 2 pays " + valatChips + " chips. Team 1 wrecked your valat");
                        }
                    }
                }
            } else if (team1wontrick) {
                //Team 2 pays
                if (getTeam1().size() == 2) {
                    //Both teams are 2 players, team 2 pays team 1 20 chips
                    teamPay(getTeam2(), getTeam1(), valatChips);
                    message("Team 2 pays " + valatChips + " chips. Team 1 valat'd them");
                } else if (getTeam1().size() == 1) {
                    //Pavenost is alone, s/he valat'd them
                    teamPay(getPovenost(), getTeam2(), valatChips);
                    message("Team 2 pays " + valatChips + " chips, Pavenost valat'd you all");
                } else {
                    //Team 2 is alone
                    teamPay(getTeam2().get(0), getTeam1(), -valatChips);
                    message("Team 2 pays " + valatChips + " chips. Team 1 valat'd you");
                }
            } else {
                //Team 1 pays
                if (getTeam1().size() == 2) {
                    //Both teams are 2 players, team 1 pays team 2 20 chips
                    teamPay(getTeam1(), getTeam2(), valatChips);
                    message("Team 1 pays " + valatChips + " chips. Team 2 valat'd them");
                } else if (getTeam1().size() == 1) {
                    //Pavenost is alone, s/he got valat'd by them
                    teamPay(getPovenost(), getTeam2(), -valatChips);
                    message("Team 1 pays " + valatChips + " chips, Pavenost got wrecked");
                } else {
                    //Team 2 is alone
                    teamPay(getTeam2().get(0), getTeam1(), valatChips);
                    message("Team 2 gets " + valatChips + " chips. Team 2 valat'd you all");
                }
            }
        }
        message("Team 1: " + getTeam1());
        message("Team 2: " + getTeam2());
        message("Results: " + getPlayers()[0] + " " + getPlayers()[0].getChips() + ", "+ getPlayers()[1] + " " + getPlayers()[1].getChips() + ", "+ getPlayers()[2] + " " + getPlayers()[2].getChips() + ", " + getPlayers()[3] + " " + getPlayers()[3].getChips());

        if (getTeam1().size() + getTeam2().size() != 4)
            throw new Error("Player count is incorrect. There should be 4 players");
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
                    message(temp.getName() + " has No Trumps, everyone pays 4");
                    chipsOwed += 4;
                case 1://fallthrough
                case 2:
                    temp.addPointCard("2 or fewer trumps");
                    message(temp.getName() + " has 2 or less trumps, everyone pays 2");
                    chipsOwed += 2;
                    break;
                case 8://fallthrough
                case 9:
                    temp.addPointCard("Little Ones");
                    message(temp.getName() + " has Little Ones, everyone pays 2");
                    chipsOwed += 2;
                    break;
                case 10:
                case 11://fallthrough
                case 12:
                    temp.addPointCard("Big ones");
                    message(temp.getName() + " has Big Ones, everyone pays 4");
                    chipsOwed += 4;
            }
            if (fiveC >= 4) {
                if (temp.hasCard("King of Spades") && temp.hasCard("King of Clubs") && temp.hasCard("King of Hearts") && temp.hasCard("King of Diamonds")) {
                    if (fiveC > 4) {
                        temp.addPointCard("All 4 Kings+");
                        message(temp.getName() + " has All 4 Kings +, everyone pays 6");
                        chipsOwed += 6;
                    } else {
                        temp.addPointCard("All 4 Kings");
                        message(temp.getName() + " has All 4 Kings, everyone pays 4");
                        chipsOwed += 4;
                    }
                } else {
                    temp.addPointCard("4 5 counts");
                    message(temp.getName() + " has 4 5 counts, everyone pays 2");
                    chipsOwed += 2;
                }
            }
            if (temp.hasCard("I") && temp.hasCard("XXI") && temp.hasCard("Škýz")) {
                temp.addPointCard("Trull");
                message(temp.getName() + " has Trull, everyone pays 2");
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
    public void valat() {
        for (Player p : players) {
            if (p.valat()) {
                for (Player t1 : team1) {
                    if (t1==p) {
                        valatTeam = 1;
                        break;
                    } else {
                        valatTeam = 2;
                    }
                }
                valat = true;
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
    public Player trick(Player currentLeader, int trickNum) throws InterruptedException {
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
        if (trickNum == 11) {
            setLostPagat(getIOTE() != -1);//If someone called the I then it's assumed lost unless they play it
            for (int i=0; i<trick.size();i++) {
                if (trick.get(i).getName().equals("I")) {
                    for (Player p : getTeam1()) {
                        if (getPlayers()[playerOffset(getLeaderLocation(), i)] == p) {
                            setPagatTeam(1);//If pagat player is on team1, set pagatTeam to 1
                        }
                    }
                    for (Player p : getTeam2()) {
                        if (getPlayers()[playerOffset(getLeaderLocation(), i)] == p) {
                            setPagatTeam(2);
                        }
                    }
                    setLostPagat(false);
                    setWonPagat(true);
                    break;
                }
            }
            if (getWonPagat()) {
                for (Card c : trick) {
                    if (c.getSuit() == Card.Suit.TRUMP && !c.getName().equals("I")) {
                        setWonPagat(false);
                        setLostPagat(true);
                        break;
                    }
                }
            }
        }//Pagat on the end
        message("\n");
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
        message(getPlayers()[playerOffset(getLeaderLocation(),winner)] + " won the trick!\n");
        Thread.sleep(getWaitTime());
        trickWinners.add(getPlayers()[playerOffset(getLeaderLocation(),winner)]);
        return getPlayers()[playerOffset(getLeaderLocation(),winner)];
    }

    public void iOnTheEnd() {
        IOTE = -1;
        for (int i=0; i<players.length;i++) {
            if (players[i].hasCard("I")) {
                if (players[i].pagat()) {
                    IOTE = i;
                }
                break;
            }
        }
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
        trickWinners = new ArrayList<>();
        if (!deck.deckSize()) {
            System.out.println("\nNEW DECK CONSTRUCTED\n");
            deck = new Deck();
        }
        if (deck.getDeck().size() != 54) {throw new Error("Illegal deck size");}
        IOTE = -1;
        wonPagat = false;
        lostPagat = false;
        valat = false;
        doublers = 0;
        pDoublers = 0;
        valatTeam = 0;
    }
    public boolean someoneHas(String cardName) {
        return players[0].hasCard(cardName) || players[1].hasCard(cardName) || players[2].hasCard(cardName) || players[3].hasCard(cardName);
    }
    public Player whoHas(String cardName) {
        //Only called if someone definitely has the card in hand
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
    public boolean anotherHand() {
        return true;
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
    public int getDoublers() {return doublers;}
    public int getPDoublers() {return pDoublers;}
    public int getIOTE() {return IOTE;}
    public boolean getWonPagat() {return wonPagat;}
    public boolean getLostPagat() {return lostPagat;}
    public int getPagatTeam() {return pagatTeam;}
    public int getRoundNumber() {return roundNumber;}

    public void setDeck(Deck d) {
        deck = d;
    }
    public void setTalon(ArrayList<Card> t) {
        talon = t;
    }
    public void setLeaderLocation(int ll) {leaderLocation = ll;}
    public void setDealer(int d) {dealer = d;}
    public void setPovenost(Player p) {povenost = p;}
    public void setLeader(Player l) {leader = l;}
    public void setTeam1Points(int t1p) {team1Points = t1p;}
    public void setDoublers(int d) {doublers = d;}
    public void setPDoublers(int p) {pDoublers = p;}
    public void setWonPagat(boolean w) {wonPagat = w;}
    public void setLostPagat(boolean l) {lostPagat = l;}
    public void setPagatTeam(int pt) {pagatTeam = pt;/*Either 1 or 2*/}
}
