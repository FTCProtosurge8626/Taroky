package us.samts.taroky;

import java.util.ArrayList;

public class AI extends Player {
    private final Table table;

    private final int numInputs = 129;
    private final double[][] weightsI = new double[numInputs][20];
    private final double[][] weightsH = new double[20][20];
    private final double[][] weightsO = new double[20][11];

    private final int[] inputs = new int[numInputs];
    private final double[] outputsI = new double[20];
    private final double[] outputsH = new double[20];

    private final int playerNumber;
    
    public AI(String name, int pn, Table t) {
        //Completely random AI, no seed

        table = t;
        //Normal player construction
        setName(name);
        setHand(new ArrayList<>());
        setWinnings(new ArrayList<>());
        setChips(100);
        resetPointCards();
        playerNumber = pn;

        double lr = 0.1;
        
        //Weight construction
        for (int i=0;i<weightsI.length;i++) {
            for (int j=0;j<weightsI[i].length;j++) {
                weightsI[i][j] = Math.random()*2*lr - lr; //Random based on learning rate
            }
        }
        for (int i=0;i<weightsH.length;i++) {
            for (int j=0;j<weightsH[i].length;j++) {
                weightsH[i][j] = Math.random()*2*lr - lr; //Random based on learning rate
            }
        }
        for (int i=0;i<weightsO.length;i++) {
            for (int j=0;j<weightsO[i].length;j++) {
                weightsO[i][j] = Math.random()*2*lr - lr; //Random based on learning rate
            }
        }
    }

    public void constructInputs(Table t) {
        /*
        * Set the inputs
        * 
        * 12 for current action varying inputs, set based on the current action (which suit was led, what card is considering to be played, etc.)
        * 54 are the cards, based on where they are (0 for unknown, 1 for in my discard, 2,3,4 in opponent's discard, 5 for in play, 6 for in hand)
        * 32 for point cards, 1/0, for each player
        * 4 for discard piles, number of cards in
        * 12 for trick winners
        * 1 for current trick winner
        * 1 more for fleck (0,1,2,3)
        * 1 for prever (0,1,2,3,4)
        * 1 for prever depth (0,1,2,3)
        * 1 for valat 0/1/2 whether it was called and if so, which team
        * 1 for valat, whether it was called
        * 1 for pagat(0/1/2/3/4, whether it was called and if so, who called it)
        * 4 for teams (0/1/2, which team each player is on, 0 for unknown)
        *   1st for this AI
        *   2nd for next player
        *   3rd for player after
        *   4th for last remaining player
        * 4 for chips (how many chips each player has)
        * 1 for shuffle (how many times the deck was shuffled at the start of the round)
        *
        */

        //First 12 are set by the action
        //Next 54 are cards (13-66)
        for (int i=13;i<13+54;i++) {
            if (inputs[i] != 1 && inputs[i] != 2 && inputs[i] != 3 && inputs[i] != 4) {
                //Only set unknown inputs. 1,2,3,4 are discard piles and last through the end of each game
                for (int j = 0; j<4;j++) {
                    if (t.getPlayers()[Table.playerOffset(playerNumber, j)].hasCard(i-13)) {
                        inputs[i] = j+1;//Adds cards to discard (1/2/3/4)
                    }
                }
            }
            if (inputs[i] != 1 && inputs[i] != 2 && inputs[i] != 3 && inputs[i] != 4) {
                //Only set unknown inputs. 1,2,3,4 are discard piles and last through the end of each game
                for (int j = 0; j<t.getTrick().size();j++) {
                    if (t.getTrick().get(j).getId() == i-13) {
                        inputs[i] = 5;//Card is in play
                        break;
                    }
                }
                for (int j=0;j<getHand().size();j++) {
                    if (getHand().get(j).getId()==i-13) {
                        inputs[i] = 6;//Card is in hand
                        break;
                    }
                }
            }
        }
        //Next 32 cards are point cards, by player (67-98)
        String[] pointCards = {"Uni","Beda","Little Ones","Big Ones","Honery","Rosa Honery","Rosa Honery+","Trull"};
        for (int i=0;i<4;i++) {
            for (int j=0;j<8;j++) {
                if (t.getPlayers()[Table.playerOffset(playerNumber,i)].hasPointCards(pointCards[j])) {
                    inputs[67+(i*8)+j] = 1;//Set to true
                }
            }
        }
        //Next 4 discard pile count, by player (99-102)
        for (int i=0;i<4;i++) {
            inputs[99 + i] = t.getPlayers()[Table.playerOffset(playerNumber,i)].getWinnings().size();
        }
        inputs[103] = Table.playerOffset(t.getLeaderLocation(),playerNumber)+1;//Leader location in relation to this AI (103)
        inputs[104] = Table.playerOffset(t.getWhoPrever(),playerNumber)+1;//Prever location in relation to this AI (104)
        inputs[105] = t.getPDoublers();//Prever depth (105)
        inputs[106] = t.getValatTeam();//Valat team (106)
        if (t.getValat()) {inputs[107] = 1;}//Valat (107)
        inputs[108] = t.getPagatTeam();//Pagat (108)
        if (hasCard(t.getPartnerCard())) {inputs[109] = 1;} else {inputs[109] = 2;}//AI knows its team (109)
        //Team numbers (110-114)
        if (t.getWhoPrever()!=0) {
            if (t.getPovenost()==t.getPlayers()[t.getWhoPrever()]) {
                //Povenost is prever
                inputs[110] = 2;//Everyone is team 2
                inputs[111] = 2;
                inputs[112] = 2;
                inputs[113] = 2;
                inputs[110+Table.playerOffset(playerNumber,t.getWhoPrever())] = 1;//Prever is team 1
            } else {
                //Everyone except prever is team 1
                inputs[110] = 1;//Everyone is team 1
                inputs[111] = 1;
                inputs[112] = 1;
                inputs[113] = 1;
                inputs[110+Table.playerOffset(playerNumber,t.getWhoPrever())] = 2;//Prever is team 2
            }
        } else {
            for (int i = 0; i < 4; i++) {
                if (t.getPovenost() == t.getPlayers()[Table.playerOffset(playerNumber, i)]) {
                    if (t.getPartnerRevealed() && t.getPPartner()==Table.playerOffset(playerNumber, i)) {
                        //Povenost is his/her own partner
                        inputs[110] = 2;
                        inputs[111] = 2;
                        inputs[112] = 2;
                        inputs[113] = 2;
                    }
                    inputs[110 + i] = 1;//Povenost is on team 1
                } else if (t.getPartnerRevealed() && t.getPPartner()==Table.playerOffset(playerNumber, i)) {
                    inputs[110 + i] = 1;//Povenost's partner
                } else if (t.getPartnerRevealed()) {
                    inputs[110 + i] = 2;//Not povenost's partner
                }
            }
        }
        //Player chips (115-118)
        for (int i=0;i<4;i++) {
            inputs[115+i] = t.getPlayers()[Table.playerOffset(playerNumber,i)].getChips();
        }
        inputs[119] = t.getShuffleCount();
    }

    public double determineOutputs(int output) {
        //Output is 1-12. Assumes construction of inputs
        for (int i=0;i<outputsI.length;i++) {
            double sum = 0;
            for (int j=0;j<inputs.length;j++) {
                sum+=inputs[j]*weightsI[j][i];
            }
            outputsI[i] = sigmoid(sum);
        }
        for (int i=0;i<outputsH.length;i++) {
            double sum = 0;
            for (int j=0;j<outputsI.length;j++) {
                sum+=outputsI[j]*weightsH[j][i];
            }
            outputsH[i] = sigmoid(sum);
        }
        double sum = 0;
        for (int i=0;i<outputsH.length;i++) {
            sum += outputsH[i] * weightsO[i][output];
        }
        return sigmoid(sum);
    }

    public static double sigmoid(double x) {
        return ((double)1/( 1 + Math.pow(Math.E,(-1*x))));
    }

    @Override
    public Deck shuffleDeck(Deck toShuffle) {
        constructInputs(table);
        for (int i=0;i<12;i++) {inputs[i]=0;}//No special inputs
        inputs[11] = 1;//Special input signals shuffling the deck

        while (determineOutputs(0)>0.9 && inputs[11] < 30) {
            inputs[11]++;
            inputs[10] = 1;
            toShuffle.shuffle((int)(determineOutputs(0)*2));//Only 0-2 work
            inputs[10] = 0;
        }
        return toShuffle;
    }

    @Override
    public int cut() {
        constructInputs(table);
        for (int i=0;i<12;i++) {inputs[i]=0;}
        inputs[11] = 1;
        return (int)(determineOutputs(1));//Any int works, so no verification needed
    }

    @Override
    public boolean goPrever() {
        constructInputs(table);
        for (int i=0;i<12;i++) {inputs[i]=0;}
        inputs[9] = 1;
        return determineOutputs(2) > 0.9;
    }

    //CURRENT LOCATION

    @Override
    public void discard() {
        constructInputs(table);

    }

    public String determinePartner() {
        constructInputs(table);
        //Can replace "hasCard" with AI choice
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

    @Override
    public Card lead() {
        constructInputs(table);
        return null;
    }

    @Override
    public Card takeTurn(Card.Suit leadingSuit) {
        constructInputs(table);
        return null;
    }

    @Override
    public boolean preverTalon(Table t) {
        constructInputs(table);
        return true;
    }

    @Override
    public boolean fleck() {
        constructInputs(table);
        return false;
    }

    @Override
    public boolean pagat() {
        constructInputs(table);
        return false;
    }

    @Override
    public boolean valat() {
        constructInputs(table);
        return false;
    }
}