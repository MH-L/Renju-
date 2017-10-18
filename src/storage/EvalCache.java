package storage;

import model.AbstractBoard;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is for caching evaluations of lines so that the engine won't be wasting time evaluating uncached lines.
 * TODO in addition to caching evaluations for lines, use a few bits to indicate whether there are threes/fours and the blocking locations for each of them
 */
public class EvalCache {
    private static final String evalDir = LocalStorage.getGameStorageBaseDir() + "lineEvals";
    private static final String fileNameTemplate = "eval_len_%s_%s.txt";

    public static void initializeCache() {
        for (int i = 5; i <= AbstractBoard.width; i++) {
            // Record files exist in pairs
            File fl = new File(evalDir + File.separator + String.format(fileNameTemplate, "black", i));
            if (fl.exists()) {
                continue;
            }
            Map<Integer, Integer> evalMapBlack = new HashMap<>();
            Map<Integer, Integer> evalMapWhite = new HashMap<>();
            Map<Integer, Byte> critMapBlack = new HashMap<>();
            Map<Integer, Byte> critMapWhite = new HashMap<>();
            String startStr = "";
            while (startStr.length() < i)
                startStr += '0';
            do {
                int lineInt = Integer.parseInt(startStr, 4);
                int[] blackCriticals = new int[2];
                int[] whiteCriticals = new int[2];
                int evalB = AbstractBoard.evaluateLine(lineInt, i, true, blackCriticals, false);
                int evalW = AbstractBoard.evaluateLine(lineInt, i, false, whiteCriticals, false);
                if (evalB > 0 && evalB < AbstractBoard.winning_score) {
                    critMapBlack.put(lineInt, (byte) (blackCriticals[0] + blackCriticals[1] * 4));
                    evalMapBlack.put(lineInt, evalB);
                }

                if (evalW > 0 && evalW < AbstractBoard.winning_score) {
                    critMapWhite.put(lineInt, (byte) (whiteCriticals[0] + whiteCriticals[1] * 4));
                    evalMapWhite.put(lineInt, evalW);
                }
            } while (!(startStr = nextStr(startStr)).isEmpty());

            writeEvals(evalMapBlack, critMapBlack, i, true);
            writeEvals(evalMapWhite, critMapWhite, i, false);
        }
    }

    private static String nextStr(String str) {
        StringBuilder sb = new StringBuilder(str);
        int counter = 0;
        while (counter < sb.length()) {
            char cur = sb.charAt(counter);
            if (cur != '3') {
                if (cur == '0')
                    sb.setCharAt(counter, '2');
                else
                    sb.setCharAt(counter, '3');
                return sb.toString();
            } else
                sb.setCharAt(counter, '0');
            counter++;
        }

        return "";
    }

    private static void writeEvals(Map<Integer, Integer> evals, Map<Integer, Byte> criticals, int length, boolean isFirst) {
        try {
            System.out.println("Writing evals: " + length + ", " + isFirst);
            String side = isFirst ? "black" : "white";
            PrintWriter pr = new PrintWriter(evalDir + File.separator + String.format(fileNameTemplate, side, length));
            for (Map.Entry<Integer, Integer> entry : evals.entrySet()) {
                byte critByte = criticals.get(entry.getKey());
                pr.println(String.format("%s|%s|%s|%s", entry.getKey(), entry.getValue(), critByte % 4, critByte / 4));
            }
            pr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns evaluation map, puts critical map
     * @param length
     * @param isFirst
     * @return
     */
    public static Map<Integer, Integer> getEvalsPutCriticals(int length, boolean isFirst, Map<Integer, Byte> criticalMap) {
        Map<Integer, Integer> returnVal = new HashMap<>();
        try {
            String side = isFirst ? "black" : "white";
            File fl = new File(evalDir + File.separator + String.format(fileNameTemplate, side, length));
            if (!fl.exists()) {
                return returnVal;
            }

            BufferedReader br = new BufferedReader(new FileReader(fl));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                returnVal.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                criticalMap.put(Integer.parseInt(parts[0]), (byte) (Integer.parseInt(parts[2]) + Integer.parseInt(parts[3]) * 4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnVal;
    }
}
