package algorithm;

import model.AbstractBoard;
import storage.LocalStorage;

import java.util.List;

public class Zobrist {
    private static long[][][] seeds = new long[AbstractBoard.height][AbstractBoard.width][2];

    public static void generateSeeds() {
        List<Long> seedsList = LocalStorage.readInitialSeed();
        int curIndex = 0;
        outer:
        for (int i = 0; i < seeds.length; i++) {
            for (int j = 0; j < seeds[0].length; j++) {
                for (int k = 0; k < seeds[0][0].length; k++) {
                    if (curIndex >= seedsList.size()) {
                        break outer;
                    }
                    seeds[i][j][k] = seedsList.get(curIndex);
                    curIndex ++;
                }
            }
        }
    }

    public static long zobristHash(int lastMove, boolean isFirst, long originalHash) {
        int ypos = lastMove / AbstractBoard.width;
        int xpos = lastMove / AbstractBoard.height;
        // TODO implement zobrist hashing for gomoku
        return isFirst ? originalHash ^ seeds[ypos][xpos][1] : originalHash ^ seeds[ypos][xpos][0];
    }
}
