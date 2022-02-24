package us.samts.taroky;

import java.io.*;

public class AIMaker extends Table {
    double[][][] initialSeed;
    FileWriter fw;
    BufferedWriter bw;
    File logs = new File("C:\\Users\\profe\\IdeaProjects\\Taroky\\src\\resources\\logs\\logs.txt");

    protected AIMaker(double[][][] seed) throws IOException {
        super(0);
        fw = new FileWriter(logs,false);
        bw = new BufferedWriter(fw);
        initialSeed = seed;
        getPlayers()[0] = new AI("Charles",0,this,seed,0.1);
        getPlayers()[1] = new AI("Danny",1,this,seed,0.1);
        getPlayers()[2] = new AI("Humphrey",2,this,seed,0.1);
        getPlayers()[3] = new AI("Dianne",3,this,seed,0.1);
    }
    protected AIMaker() throws IOException {
        super(0);
        fw = new FileWriter(logs,true);
        bw = new BufferedWriter(fw);
        getPlayers()[0] = new AI("Charles",0,this);
        getPlayers()[1] = new AI("Danny",1,this);
        getPlayers()[2] = new AI("Humphrey",2,this);
        getPlayers()[3] = new AI("Dianne",3,this);
    }

    @Override
    public boolean anotherHand() {
        if (getRoundNumber()%100 == 0) {
            int best = 0;
            for (int i=1;i<4;i++) {
                if (getPlayers()[i].getChips() > getPlayers()[best].getChips()) {
                    best = i;
                }
            }
            initialSeed = ((AI) getPlayers()[best] ).getSeed();
            if (getRoundNumber()%10000 == 0) {
                System.out.println(printSeed(initialSeed));
                System.out.println(getPlayers()[0] + ": " + getPlayers()[0].getChips() + ", " + getPlayers()[1] + ": " + getPlayers()[1].getChips() + ", " + getPlayers()[2] + ": " + getPlayers()[2].getChips() + ", " + getPlayers()[3] + ": " + getPlayers()[3].getChips());
            }
            resetAI();
        }
        return true;
    }

    public void resetAI() {
        getPlayers()[0] = new AI("Charles",0,this,initialSeed);
        getPlayers()[1] = new AI("Danny",1,this,initialSeed,0.1);
        getPlayers()[2] = new AI("Humphrey",2,this,initialSeed,0.1);
        getPlayers()[3] = new AI("Dianne",3,this,initialSeed,0.1);
    }

    public static String printSeed(double[][][] toPrint) {
        StringBuilder print = new StringBuilder("{");
        for (double[][] z : toPrint) {
            print.append("{");
            for (double[] x: z) {
                print.append("{");
                for (double y: x) {
                    print.append(y).append(",");
                }
                print = new StringBuilder(print.substring(0, print.length() - 1));
                print.append("},");
            }
            print = new StringBuilder(print.substring(0, print.length() - 1));
            print.append("},");
        }
        print = new StringBuilder(print.substring(0, print.length() - 1));
        print.append("}");
        return print.toString();
    }

    @Override
    public void message(String message) {
        //System.out.println(message);
        /*try {
            bw.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public String getInputString(String message) {
        return null;
    }

    @Override
    public boolean getInputBoolean(String message) {
        return false;
    }

    @Override
    public int getInputInt(String message) {
        return 0;
    }

}
