package algorithm;

import model.AbstractBoard;
import storage.LocalStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return isFirst ? originalHash ^ seeds[ypos][xpos][1] : originalHash ^ seeds[ypos][xpos][0];
    }

    public static Map<Long, StatObj> getStatMap(List<List<Integer>> games, List<Integer> results) {
        Map<Long, StatObj> retVal = new HashMap<>();
        for (int i = 0; i < games.size(); i++) {
            List<Integer> curGame = games.get(i);
            int curResult = results.get(i);
            long currentHash = 0;
            for (int j = 0; j < curGame.size(); j++) {
                int lastMove = curGame.get(j);
                if (!retVal.containsKey(currentHash)) {
                    retVal.put(currentHash, new StatObj(0,0,0));
                }

                if (curResult == 1) {
                    retVal.get(currentHash).wins++;
                } else if (curResult == 2) {
                    retVal.get(currentHash).losses++;
                } else {
                    retVal.get(currentHash).ties++;
                }
                currentHash = zobristHash(lastMove, j % 2 == 0, currentHash);
            }
            // No need to do it again since everyone knows the result of the last move.
        }
        return retVal;
    }
}
