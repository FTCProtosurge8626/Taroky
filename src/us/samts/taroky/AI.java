package us.samts.taroky;

import java.util.ArrayList;

public class AI extends Player {
    private int[] inputs; //What information the bot has access to
    private int[][] weights; //What the bot does to the information
    private int numOutputs = 9;

    public AI(int[][] seed, int learningFactor) {
        inputs = new int[30];
        weights = new int[numOutputs][inputs.length];
        for (int i=0;i<weights.length;i++) {
            for (int j=0; j<weights[i].length;j++) {
                if (i < seed.length && j < seed[i].length) {
                    weights[i][j] = seed[i][j] + (int) (Math.random() * 2 * learningFactor) - learningFactor;
                } else {
                    weights[i][j] = (int) (Math.random() * 2 * learningFactor) - learningFactor;
                }
            }
        }
    }
    public AI(int[][] seed) {
        inputs = new int[30];
        weights = new int[numOutputs][inputs.length];
        for (int i=0;i<weights.length;i++) {
            for (int j=0; j<weights[i].length;j++) {
                if (i < seed.length && j < seed[i].length) {
                    weights[i][j] = seed[i][j];
                } else {
                    weights[i][j] = (int) (Math.random() * 2) - 1;
                }
            }
        }
    }
    public AI() {
        inputs = new int[30];
        weights = new int[numOutputs][inputs.length];
        for (int i=0;i<weights.length;i++) {
            for (int j=0; j<weights[i].length;j++) {
                weights[i][j] = (int) (Math.random() * 2) - 1;
            }
        }
    }

    public void resetInputs() {
        //Set the inputs
        /*
        * 1-12 are cards in hand
        * 13-16 discarded cards
        * 17-124 who played what
        *
        * */
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
    public void deal(int style, Table t) {
        //Doesn't need tbd
    }

    @Override
    public boolean goPrever() {
        return false;
    }

    @Override
    public void drawTalon(int x, Table t) {
        //Doesn't need tbd
    }

    @Override
    public ArrayList<Card> discard() {
        return null;
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
    public String determinePartner() {
        //Doesn't need tbd
        return null;
    }

    @Override
    public boolean preverTalon(Table t) {
        return false;
    }

    @Override
    public boolean fleck() {
        return false;
    }

    @Override
    public boolean pagat() {
        return false;
    }
}