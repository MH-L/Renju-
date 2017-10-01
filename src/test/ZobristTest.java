package test;

import algorithm.BoardTree;
import algorithm.Zobrist;
import model.*;
import storage.LocalStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import org.junit.Before;
//import org.junit.Test;

public class ZobristTest {
    AbstractBoard bd1;
    AbstractBoard bd2;

    public void initialize() {
        bd1 = new UnrestrictedBoard();
        bd2 = new UnrestrictedBoard();
        Zobrist.generateSeeds();
    }

//    @Test
    public void testHashing() {
        bd1.updateBoard(98, true);
        bd1.updateBoard(128, false);
        System.out.println(bd1.getZobristHash());
        bd1.reset();
        bd1.updateBoard(98, true);
        bd1.updateBoard(128, true);
        System.out.println(bd1.getZobristHash());
    }

    public static void main(String[] args) throws IOException {
        LocalStorage.initializeLocalStorage();
        LocalStorage.writeInitialSeed();
        Main.machineLearningSetup();
        UnrestrictedCvCGame experiment = new UnrestrictedCvCGame(new UnrestrictedBoard(),
                AbstractGame.Difficulty.custom, AbstractGame.Difficulty.custom);
        experiment.setCustomAIParams(3, 3);
        experiment.runCvCGameForRecord(100000);
//        experiment.runCvCGameForRecord(10);
//        System.out.println("Executing command in linux...");
//        Runtime.getRuntime().exec("nohup java -jar ~/binaries/Renju.jar &");
//        System.exit(0);
    }
}
