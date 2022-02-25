package us.samts.taroky;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AITournament {
    //AI tournament will host a set number of AI tables. The tournament will create the AI. The tables will test the AI.
    private int numTables;
    private AIMaker[] tables;
    public AITournament() {}



    public void runTournament(double[][][] seed, int nTables, int roundsPerGen) throws InterruptedException {
        while (true) {
            startTournament(seed,nTables);
            seed = playTournament(roundsPerGen);
            System.out.println(AIMaker.printSeed(seed));
        }
    }
    public void startTournament(double[][][] seed, int nTables) {
        numTables = nTables;
        tables = new AIMaker[numTables];
        //The first table has the seed AI, a robot, and two offset AI
        if (numTables > 0) {
            tables[0] = new AIMaker(false);
            tables[0].setAI(
                    new AI[] {
                            new AI("Charles",0,tables[0],seed),
                            new AI("Henry",1,tables[0],seed,0.1),
                            new AI("Jace",2,tables[0],seed,0.01),
                            new AI("Dian",3,tables[0],seed,1)
                    }
            );
        }
        for (int i=1;i<numTables;i++) {
            tables[i] = new AIMaker(false);
            tables[i].setAI(
                    new AI[] {
                            new AI("Henry",0,tables[i],seed,0.1),
                            new AI("Henry",1,tables[i],seed,0.1),
                            new AI("Henry",2,tables[i],seed,0.1),
                            new AI("Henry",3,tables[i],seed,0.1)
                    }
            );
        }
        //Tables are now filled
    }
    public double[][][] playTournament(int numRounds) throws InterruptedException {
        int rounds = 0;
        while (rounds < numRounds) {
                //Play 1 game at each table
                for (int i = 0; i < numTables; i++) {
                    tables[i].startGame();
                }
            //Get the AI and randomize the tables' players
            ArrayList<Player> players = new ArrayList<>();
            for (int i = 0; i < numTables; i++) {
                players.addAll(List.of(tables[i].getAI()));
            }
            for (int i = 0; i < tables.length; i++) {
                tables[i].resetTable();
                tables[i] = new AIMaker(false);
                tables[i].setAI(new AI[]{
                        (AI) players.remove((int) (Math.random() * players.size())),
                        (AI) players.remove((int) (Math.random() * players.size())),
                        (AI) players.remove((int) (Math.random() * players.size())),
                        (AI) players.remove((int) (Math.random() * players.size()))
                });
                tables[i].resetTable();
            }
            rounds++;
        }
        double[][][] bestSeed = new double[0][0][0];
        int bestChips = 0;
        ArrayList<AI> players = new ArrayList<>();
        for (int i = 0; i < numTables; i++) {
            players.addAll(List.of(tables[0].getAI()));
        }
        for (AI a : players) {
            if (a.getChips() > bestChips)
                bestSeed = a.getSeed();
        }
        if (Arrays.deepEquals(bestSeed, new double[0][0][0]))
            throw new Error("No best seed was chosen");
        return bestSeed;
    }

}
