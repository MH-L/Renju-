package algorithm;

import storage.LocalStorage;

import java.util.List;

public class Zobrist {
    private static int[][][] seeds;

    public static void generateSeeds() {
        List<Long> seedsList = LocalStorage.readInitialSeed();
        
    }
}
