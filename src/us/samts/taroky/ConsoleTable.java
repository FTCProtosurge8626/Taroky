package us.samts.taroky;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleTable extends Table {
    private static boolean print;
    private Scanner s;

    public ConsoleTable() {
        super(100);
        s = new Scanner(System.in);
        System.out.println("What's your name?");
        getPlayers()[0] = new Human(s.nextLine(), this);
        getPlayers()[1] = new Robot("Samuel");
        getPlayers()[2] = new Robot("Daniel");
        getPlayers()[3] = new Robot("Benjamin");
        print = true;
    }
    public void stats() {
        System.out.println(getRoundNumber() + " results: " + getPlayers()[0] + " " + getPlayers()[0].getChips() + ", "+ getPlayers()[1] + " " + getPlayers()[1].getChips() + ", "+ getPlayers()[2] + " " + getPlayers()[2].getChips() + ", " + getPlayers()[3] + " " + getPlayers()[3].getChips());
    }
    public void message(String message) {
        if (print) {System.out.println(message);}
    }

    @Override
    public String getInputString(String message) {
        return s.nextLine();
    }

    @Override
    public boolean getInputBoolean(String message) {
        //Assuming (y/n)
        System.out.print("(y/n)");
        return s.nextLine().contains("y");
    }

    @Override
    public int getInputInt(String message) {
        int temp = s.nextInt();
        s.nextLine();
        return temp;
    }

    @Override
    public boolean anotherHand() {
        if (print) {
            System.out.println("Another hand? (y/n)");
            return s.nextLine().contains("y");
        } else {
            if (getRoundNumber() % 100000 == 0)
                stats();
            return true;
        }
    }

    public static boolean getPrint() {
        return print;
    }
}