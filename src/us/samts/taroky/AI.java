package us.samts.taroky;

import java.util.ArrayList;

public class AI extends Player {
    private final Table table;

    private final int numInputs = 120;
    private double[][] weightsI = new double[numInputs][20];
    private double[][] weightsH = new double[20][20];
    private double[][] weightsO = new double[20][11];

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
    public AI(String name, int pn, Table t, double[][][] seed) {
        //Set AI, based on seed

        table = t;
        //Normal player construction
        setName(name);
        setHand(new ArrayList<>());
        setWinnings(new ArrayList<>());
        setChips(100);
        resetPointCards();
        playerNumber = pn;

        weightsI = seed[0];
        weightsH = seed[1];
        weightsO = seed[2];
    }
    public AI(String name, int pn, Table t, double[][][] seed, double lr) {
        //Completely random AI, based on seed

        table = t;
        //Normal player construction
        setName(name);
        setHand(new ArrayList<>());
        setWinnings(new ArrayList<>());
        setChips(100);
        resetPointCards();
        playerNumber = pn;

        //Weight construction
        for (int i=0;i<weightsI.length;i++) {
            for (int j=0;j<weightsI[i].length;j++) {
                weightsI[i][j] = seed[0][i][j] + Math.random()*2*lr - lr; //Random based on learning rate
            }
        }
        for (int i=0;i<weightsH.length;i++) {
            for (int j=0;j<weightsH[i].length;j++) {
                weightsH[i][j] = seed[1][i][j] + Math.random()*2*lr - lr; //Random based on learning rate
            }
        }
        for (int i=0;i<weightsO.length;i++) {
            for (int j=0;j<weightsO[i].length;j++) {
                weightsO[i][j] = seed[2][i][j] + Math.random()*2*lr - lr; //Random based on learning rate
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
        int chipAbs = 0;
        for (int i=0;i<4;i++) {
            chipAbs += Math.abs(t.getPlayers()[Table.playerOffset(playerNumber,i)].getChips());
        }
        for (int i=0;i<4;i++) {
            inputs[115+i] = t.getPlayers()[Table.playerOffset(playerNumber,i)].getChips() / chipAbs;//Normalize output
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

    public double[][][] getSeed() {
        return new double[][][] {
                weightsI,
                weightsH,
                weightsO
        };
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
    }//Output 0

    @Override
    public int cut() {
        constructInputs(table);
        for (int i=0;i<12;i++) {inputs[i]=0;}
        inputs[11] = 1;
        return (int)(determineOutputs(1));//Any int works, so no verification needed
    }//Output 1

    @Override
    public boolean goPrever() {
        constructInputs(table);
        for (int i=0;i<12;i++) {inputs[i]=0;}
        inputs[9] = 1;
        return determineOutputs(2) > 0.9;
    }//Output 2

    @Override
    public void discard() {
        constructInputs(table);
        while (getHand().size() > 12) {
            inputs[11] = getHand().size() - 12;//Set the final custom input to the number of cards remaining
            ArrayList<Card> discardable = new ArrayList<>(getHand());
            for (int i=discardable.size()-1;i>=0;i--) {
                if (discardable.get(i).getSuit()==Card.Suit.TRUMP || discardable.get(i).getPointValue()==5) {
                    discardable.remove(i);
                }
            }
            if (discardable.size()==0) {
                discardable = new ArrayList<>(getHand());
                for (int i=discardable.size()-1;i>=0;i--) {
                    if (discardable.get(i).getPointValue()==5) {
                        discardable.remove(i);
                    }
                }
            }
            if (discardable.size()<=inputs[11]) {
                //If the number of discardable cards is less than or equal to the number of cards needed to discard, then discard them all
                for (int i=getHand().size()-1;i>=0;i--) {
                    if (discardable.contains(getHand().get(i))) {
                        getWinnings().add(getHand().remove(i));
                    }
                }
            } else {
                //AI makes a choice for what to discard
                //Tell the AI what cards are discardable
                for (int i=0;i<discardable.size();i++) {
                    inputs[i] = discardable.get(i).getId();
                }
                //Rank the cards by sigmoid, then discard the ones with the highest values
                double[] discardRank = new double[discardable.size()];
                for (int i=0;i<discardable.size();i++) {
                    inputs[10] = discardable.get(i).getId();
                    discardRank[i] = determineOutputs(3);
                }
                //Snipe out the lowest ranked card
                int lowest = 0;
                for (int i=1;i<discardRank.length;i++) {
                    if (discardRank[lowest] > discardRank[i]) {
                        lowest = i;
                    }
                }
                getWinnings().add(getHand().remove(lowest));
            }
        }
    }//Output 3

    public String determinePartner() {
        constructInputs(table);
        //Can replace "hasCard" with AI choice
        for (int i=0;i<12;i++) {
            inputs[i] = 0;
        }
        if (determineOutputs(4) > 0.9) { //Play alone?
            return "XIX";
        } else if (!hasCard("XIX")) {
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
    }//Output 4

    @Override
    public Card lead() {
        constructInputs(table);
        for (int i=0;i<12;i++) {
            if (i<getHand().size()) {
                inputs[i] = getHand().get(i).getId();
            } else {
                inputs[i] = 0;
            }
        }
        int toPlay = (int)(determineOutputs(5)*getHand().size());
        if (toPlay == getHand().size()) {toPlay--;}//If the AI is 100% sure it wants to play the last card, it can overflow. This prevents that while still following the AI's intentions
        return getHand().remove(toPlay);
    }//Output 5

    @Override
    public Card takeTurn(Card.Suit leadingSuit) {
        constructInputs(table);
        ArrayList<Card> playable = new ArrayList<>();
        for (Card c : getHand()) {
            if (c.getSuit()==leadingSuit) {
                playable.add(c);
            }
        }
        if (playable.size()==0) {
            for (Card c : getHand()) {
                if (c.getSuit()==Card.Suit.TRUMP) {
                    playable.add(c);
                }
            }
        }
        if (playable.size()==0) {
            playable.addAll(getHand());
        }
        //Choose a playable card
        int toPlayN = (int)(determineOutputs(6)*playable.size());
        if (toPlayN == playable.size()) {toPlayN--;}
        Card toPlay = playable.remove(toPlayN);
        for (int i=0;i<getHand().size();i++) {
            if (getHand().get(i)==toPlay) {
                getHand().remove(i);
                break;
            }
        }
        return toPlay;
    }//Output 6

    @Override
    public boolean preverTalon(Table t) {
        constructInputs(table);
        for (int i=0;i<12;i++) {inputs[i]=0;}
        return determineOutputs(7)>0.9;
    }//Output 7

    @Override
    public boolean fleck() {
        constructInputs(table);
        for (int i=0;i<12;i++) {inputs[i]=0;}
        return determineOutputs(8)>0.9;
    }//Output 8

    @Override
    public boolean pagat() {
        constructInputs(table);
        for (int i=0;i<12;i++) {inputs[i]=0;}
        return determineOutputs(9)>0.9;
    }//Output 9

    @Override
    public boolean valat() {
        constructInputs(table);
        for (int i=0;i<12;i++) {inputs[i]=0;}
        return determineOutputs(10)>0.9;
    }//Output 10
}

/*
 * The AI will be composed as follows:
 * Each AI will have inputs, as shown in the constructInputs() method in the AI class
 * These inputs will lead to 20 "hidden" neurons
 * These neurons will lead to 20 additional "hidden" neurons
 * These will lead to the 10 outputs
 *
 * Each output is what decision the AI makes when the Table requests an action. These include
 *   Shuffle pattern     (0,1,2,3 on repeat and for how long)
 *   Cut choice          (1,2,3,4,5,6,12,345)
 *   Prever choice       (boolean)
 *   Prever Talon        (Whether to keep the given Talon or swap)
 *   Discard             (Which cards to discard)
 *   Fleck               (boolean)
 *   Pagat               (boolean)
 *   Valat               (boolean)
 *   Determine Partner   (Whether to play by itself when it has the XIX and is pavenost)
 *   Lead                (Which card to lead)
 *   TakeTurn            (Which card to play on its turn)
 *
 * These choices will determine how "good" the AI is
 * The AI will each play 10,000 rounds, then the highest scoring AI will become the seed.
 * The seed will continue on, with the other AI being slight variations of it
 *
 * AIMaker will run this, and after each round will print the seed
 * This seed can be pasted into the initialSeed variable to start off the next generation if there is a pause between runs
 *
 * Neural Network:
 * INPUTS (A lot, currently 120)
 * HIDDEN 20
 * HIDDEN 20
 * OUTPUTS 11
 *
 * Connections:
 * I>H1  =
 * H1>H2 = 400
 * H2>O  = 232
 * weightI[inputs][20]
 * weightsH[20][20]
 * weightsO[20][11]
 *
 * outputsI[20]
 * outputsH[20]
 *
 * */