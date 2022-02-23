package us.samts.taroky;

import java.util.ArrayList;

public class AI extends Player {
    private final int numInputs = 129;
    private double[][] weightI = new double[numInputs][20];
    private double[][] weightsH = new double[20][20];
    private double[][] weightsO = new double[20][11];

    private int[] inputs = new int[numInputs];
    private double[] outputsI = new double[20];
    private double[] outputsH = new double[20];

    private int playerNumber;
    
    public AI(String name, int pn) {
        //Completely random AI, no seed

        //Normal player construction
        setName(name);
        setHand(new ArrayList<>());
        setWinnings(new ArrayList<>());
        setChips(100);
        resetPointCards();
        playerNumber = pn;

        double lr = 0.1;
        
        //Weight construction
        for (int i=0;i<weightI.length;i++) {
            for (int j=0;j<weightI[i].length;j++) {
                weightI[i][j] = Math.random()*2*lr - lr; //Random based on learning rate
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
        * 1 for valat (0/1/2/3/4, whether it was called and if so, who called it)
        * 1 for pagat(0/1/2/3/4, whether it was called and if so, who called it)
        * 4 for teams (0/1/2, which team each player is on, 0 for unknown)
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
        //Next 32 cards are point cards, by player (67-70)
        String[] pointCards = {"Uni","Beda","Little Ones","Big Ones","Honery","Rosa Honery","Rosa Honery+","Trull"};
        for (int i=0;i<4;i++) {
            for (int j=0;j<8;j++) {
                if (t.getPlayers()[Table.playerOffset(playerNumber,i)].hasPointCards(pointCards[j])) {
                    inputs[67+(i*8)+j] = 1;//Set to true
                }
            }
        }
        //Next 4 discard pile count, by player (71-74)
        for (int i=0;i<4;i++) {
            inputs[71 + i] = t.getPlayers()[Table.playerOffset(playerNumber,i)].getWinnings().size();
        }
        inputs[75] = Table.playerOffset(t.getLeaderLocation(),playerNumber);//Leader location in relation to this AI (75)

    }
    public static double sigmoid(double x) {
        return ((double)1/( 1 + Math.pow(Math.E,(-1*x))));
    }

    @Override
    public Deck shuffleDeck(Deck toShuffle) {
        return null;
    }

    @Override
    public int cut() {
        return 0;
    }

    @Override
    public boolean goPrever() {
        return false;
    }

    @Override
    public void discard() {
    }

    public String determinePartner() {
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
        return null;
    }

    @Override
    public Card takeTurn(Card.Suit leadingSuit) {
        return null;
    }

    @Override
    public boolean preverTalon(Table t) {
        return true;
    }

    @Override
    public boolean fleck() {
        return false;
    }

    @Override
    public boolean pagat() {
        return false;
    }

    @Override
    public boolean valat() {
        return false;
    }
}