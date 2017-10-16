package model;

import algorithm.Zobrist;
import storage.EvalCache;
import storage.LocalStorage;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class AbstractBoard {
    // TODO remove this after mature
    public static long totalEvals = 0;
    public static long cachedEvals = 0;

	public static final int width = 15;
	public static final int height = 15;
	protected static final int invalid_location = 225;
	protected static final int sente_stone = 3;
	protected static final int gote_stone = 2;
	
	public static final int winning_score = 1000000;
	protected static final int open_four = 8000;
	protected static final int three_four = 2000;
	protected static final int four_four = 2000;
	protected static final int ojump_four = 20;
	protected static final int jump_four = 13;
	protected static final int closed_four = 15;
	protected static final int three_three = 200;
	protected static final int open_three = 10;
	protected static final int jump_three = 8;
	protected static final int closed_three = 4;
	protected static final int cjump_three = 3;
	protected static final int open_two = 5;
	protected static final int small_jump_two = 2;
	protected static final int big_jump_two = 1;
	
	protected int[] rowBased;
	protected int[] rowBasedEval;
	protected int[] colBased;
	protected int[] colBasedEval;
	protected int[] ltorDiag;
	protected int[] ltorDiagEval;
	protected int[] rtolDiag;
	protected int[] rtolDiagEval;

	// Stores the heuristic in terms of black by summing up all lines
	private int curHeuristic = 0;

	// Stores number of threes and fours currently on board for white
	private int totWhiteThree = 0;
	private int totWhiteFour = 0;

	// Stores number of threes and fours currently on board for black
	private int totBlackThree = 0;
	private int totBlackFour = 0;

	// stores number of white threes in each line
    // TODO here, add bit-shifting to represent the blocking locations
	private int[] whiteThree;

	// stores number of white fours in each line
    // TODO here, add bit-shifting to represent the blocking locations
	private int[] whiteFour;

	// stores number of black threes in each line
	private int[] blackThree;

	// stores number of black fours in each line
	private int[] blackFour;
	protected int lastMove = invalid_location;
	protected long zobristHash = 0;
	
	protected Map<Integer, List<Integer>> adjacentMap;
	protected Map<Integer, List<Integer>> adjacentMapRed;
	protected Random rng;
	public static List<Map<Integer, Integer>> evalMapsBlack;
	public static List<Map<Integer, Integer>> evalMapsWhite;
	private static List<Map<Integer, Byte>> criticalMapsWhite;
	private static List<Map<Integer, Byte>> criticalMapsBlack;
	private List<Integer> moveSequence;

	static {
        evalMapsBlack = new ArrayList<>(width + 1);
        evalMapsWhite = new ArrayList<>(width + 1);
        criticalMapsBlack = new ArrayList<>(width + 1);
        criticalMapsWhite = new ArrayList<>(width + 1);
        for (int i = 0; i <= width; i++) {
            evalMapsBlack.add(new HashMap<>());
            evalMapsWhite.add(new HashMap<>());
            criticalMapsBlack.add(new HashMap<>());
            criticalMapsWhite.add(new HashMap<>());
        }
    }

    private static void initializeCachedEvals() {
	    EvalCache.initializeCache();
	    for (int i = 5; i <= width; i++) {
            // Initialize eval maps and critial maps using cache
            evalMapsBlack.set(i, EvalCache.getEvalsPutCriticals(i, true, criticalMapsBlack.get(i)));
            evalMapsWhite.set(i, EvalCache.getEvalsPutCriticals(i, false, criticalMapsWhite.get(i)));
        }
    }
	
	public AbstractBoard() {
	    initializeCachedEvals();
		rng = new Random();
		moveSequence = new ArrayList<>();
		rowBased = new int[height];
		colBased = new int[width];
		ltorDiag = new int[width + height - 1];
		rtolDiag = new int[width + height - 1];
		rowBasedEval = new int[height];
		colBasedEval = new int[width];
		ltorDiagEval = new int[width + height - 1];
		rtolDiagEval = new int[width + height - 1];
		whiteThree = new int[3*width + 3*height - 2];
        whiteFour = new int[3*width + 3*height - 2];
        blackThree = new int[3*width + 3*height - 2];
        blackFour = new int[3*width + 3*height - 2];
		adjacentMap = new HashMap<>();
		adjacentMapRed = new HashMap<>();
		generateAdjacentMoves();
		genAdjMovesReduced();
	}
	
	/**
     * CORE FUNCTION
	 * Update board with given location: 00 for empty location,
	 * 10 for white stone, 11 for black stone (in binary)
	 * @param location location of stone
	 * @param first whether or not the player is first
	 * @return True if update successful; false otherwise
	 */
	public boolean updateBoard(int location, boolean first) {
		if (location < 0 || location >= invalid_location)
			return false;
		int rowIndex = location / width;
		int colIndex = location % width;
		int ltorIndex = rowIndex - colIndex + width - 1;
		int rtolIndex = rowIndex + colIndex;
		int origRow = rowBased[rowIndex];
		int origCol = colBased[colIndex];
		int origLtoRDiag = ltorDiag[ltorIndex];
		int origRtoLDiag = rtolDiag[rtolIndex];

		int indexOnLtoRDiag = ltorIndex < width ? rowIndex : colIndex;
		int indexOnRtoLDiag = rtolIndex < width ? rowIndex : width - 1 - colIndex;

		if (!isSquareEmpty(location))
			return false;
		
		int stone = first ? sente_stone : gote_stone;

		origRow = origRow ^ (stone << colIndex * 2);
		rowBased[rowIndex] = origRow;

		origCol = origCol ^ (stone << rowIndex * 2);
		colBased[colIndex] = origCol;

		origLtoRDiag = origLtoRDiag ^ (stone << indexOnLtoRDiag * 2);
		ltorDiag[ltorIndex] = origLtoRDiag;

		origRtoLDiag = origRtoLDiag ^ (stone << indexOnRtoLDiag * 2);
		rtolDiag[rtolIndex] = origRtoLDiag;

//		zobristHash = Zobrist.zobristHash(location, first, zobristHash);

        // After everything gets updated, update heuristics as well.
        updateHeuristics(location);
		return true;
	}
	
	public void reset() {
		for (int i = 0; i < rowBased.length; i++) {
			rowBased[i] = 0;
		}
		
		for (int i = 0; i < colBased.length; i++) {
			colBased[i] = 0;
		}
		
		for (int i = 0; i < ltorDiag.length; i++) {
			ltorDiag[i] = 0;
		}
		
		for (int i = 0; i < rtolDiag.length; i++) {
			rtolDiag[i] = 0;
		}

		// Reset hash and clear move sequences.
		zobristHash = 0;
		moveSequence.clear();
		// TODO clear cached stuffs
	}
	
	public void render() {
		char firstPlayerChar = '\u25CF';
		char secondPlayerChar = '\u25CB';
		char emptyLocChar = '\u25A1';
		System.out.println("   A B C D E F G H I J K L M N O");
		for (int i = 0; i < rowBased.length; i++) {
			System.out.print(i + 1);
			int curRow = rowBased[i];
			if (i < 9)
				System.out.print("\u0020\u0020");
			else
				System.out.print("\u0020");
			for (int j = 0; j < colBased.length; j++) {
				int andingResult = curRow & (3 << (j * 2));
				if (andingResult == (3 << (j * 2)))
					System.out.print(firstPlayerChar + "\u0020");
				else if (andingResult == (2 << (j * 2)))
					System.out.print(secondPlayerChar + "\u0020");
				else
					System.out.print(emptyLocChar + "\u0020");
			}
			
			System.out.println();
		}
	}

    /**
     * Deprecated, use <code>getHeuristics</code> instead
     * @return Board heuristics
     */
	@Deprecated
	public int evaluateBoard() {
		return evaluateBoardPV(true) - evaluateBoardPV(false);
	}
	
	/**
	 * Add some random perturbation to board evalulation function
	 * so that the moves will have some randomness.
     *
     * Newer versions are deprecating this. It is a misunderstanding of randomness in strategy.
	 * @param oscillation
	 * @return
	 */
	@Deprecated
	public int evaluateBoardRng(int oscillation) {
		if (oscillation == 0)
			return evaluateBoardPV(true) - evaluateBoardPV(false);
		int randomPurt = rng.nextInt(oscillation * 2 + 1);
		randomPurt -= oscillation;
		return evaluateBoardPV(true) - evaluateBoardPV(false) + randomPurt;
	}

    /**
     * CORE FUNCTION
     * Update board heuristics and line heuristics; called after update board or withdraw move.
     * @param lastMove the most recent move (either to update or withdraw)
     */
	private void updateHeuristics(int lastMove) {
	    // TODO things to consider: what if after withdrawal the composite pattern is gone? -- solved
	    int rowIndex = lastMove / width;
	    int colIndex = lastMove % width;
	    int ltorDiagIndex = getltorDiagIndex(lastMove);
	    int rtolDiagIndex = getrtolDiagIndex(lastMove);

	    // Evaluate for black
	    int[] rowBlack = new int[2];
	    int[] colBlack = new int[2];
	    int rowHeuristics = evaluateLine(rowBased[rowIndex], width, true, rowBlack);
	    int colHeuristics = evaluateLine(colBased[colIndex], height, true, colBlack);
	    int[] ltorBlack = new int[2];
	    int[] rtolBlack = new int[2];
        int ltorHeuristics = evaluateLine(ltorDiag[ltorDiagIndex],
                Math.min(ltorDiagIndex + 1, width + height - 1 - ltorDiagIndex), true, ltorBlack);
        int rtolHeuristics = evaluateLine(rtolDiag[rtolDiagIndex],
                Math.min(rtolDiagIndex + 1, width + height - 1 - rtolDiagIndex), true, rtolBlack);

        // Evaluate for white
        int[] rowWhite = new int[2];
        int[] colWhite = new int[2];
        int rowAntiHeu = evaluateLine(rowBased[rowIndex], width, false, rowWhite);
        int colAntiHeu = evaluateLine(colBased[colIndex], height, false, colWhite);
        int[] ltorWhite = new int[2];
        int[] rtolWhite = new int[2];
        int ltorAntiHeu = evaluateLine(ltorDiag[ltorDiagIndex],
                Math.min(ltorDiagIndex + 1, width + height - 1 - ltorDiagIndex), false, ltorWhite);
        int rtolAntiHeu = evaluateLine(rtolDiag[rtolDiagIndex],
                Math.min(rtolDiagIndex + 1, width + height - 1 - rtolDiagIndex), false, rtolWhite);

        // For detecting composite patterns
        int whiteOrigTotThree = whiteThree[rowIndex] + whiteThree[height + colIndex] + whiteThree[width + height + ltorDiagIndex]
                + whiteThree[2*width + 2*height - 1 + rtolDiagIndex];
        int whiteOrigTotFour = whiteFour[rowIndex] + whiteFour[height + colIndex] + whiteFour[width + height + ltorDiagIndex]
                + whiteFour[2*width + 2*height - 1 + rtolDiagIndex];

        int blackOrigTotThree = blackThree[rowIndex] + blackThree[height + colIndex] + blackThree[width + height + ltorDiagIndex]
                + blackThree[2*width + 2*height - 1 + rtolDiagIndex];
        int blackOrigTotFour = blackFour[rowIndex] + blackFour[height + colIndex] + blackFour[width + height + ltorDiagIndex]
                + blackFour[2*width + 2*height - 1 + rtolDiagIndex];

        int whiteCurTotThree = rowWhite[0] + colWhite[0] + ltorWhite[0] + rtolWhite[0];
        int whiteCurTotFour = rowWhite[1] + colWhite[1] + ltorWhite[1] + rtolWhite[1];
        int blackCurTotThree = rowBlack[0] + colBlack[0] + ltorBlack[0] + rtolBlack[0];
        int blackCurTotFour = rowBlack[1] + colBlack[1] + ltorBlack[1] + rtolBlack[1];

        whiteThree[rowIndex] = rowWhite[0];
        whiteThree[height + colIndex] = colWhite[0];
        whiteThree[width + height + ltorDiagIndex] = ltorWhite[0];
        whiteThree[2*width + 2*height - 1 + rtolDiagIndex] = rtolWhite[0];
        whiteFour[rowIndex] = rowWhite[1];
        whiteFour[height + colIndex] = colWhite[1];
        whiteFour[width + height + ltorDiagIndex] = ltorWhite[1];
        whiteFour[2*width + 2*height - 1 + rtolDiagIndex] = rtolWhite[1];

        blackThree[rowIndex] = rowBlack[0];
        blackThree[height + colIndex] = colBlack[0];
        blackThree[width + height + ltorDiagIndex] = ltorBlack[0];
        blackThree[2*width + 2*height - 1 + rtolDiagIndex] = rtolBlack[0];
        blackFour[rowIndex] = rowBlack[1];
        blackFour[height + colIndex] = colBlack[1];
        blackFour[width + height + ltorDiagIndex] = ltorBlack[1];
        blackFour[2*width + 2*height  - 1+ rtolDiagIndex] = rtolBlack[1];

        // TODO update board heuristics as well as totals (3&4)
        totWhiteThree = totWhiteThree - whiteOrigTotThree + whiteCurTotThree;
        totWhiteFour = totWhiteFour - whiteOrigTotFour + whiteCurTotFour;
        totBlackThree = totBlackThree - blackOrigTotThree + blackCurTotThree;
        totBlackFour = totBlackFour - blackOrigTotFour + blackCurTotFour;

        int prevTotEval = rowBasedEval[rowIndex] + colBasedEval[colIndex] + ltorDiagEval[ltorDiagIndex]
                + rtolDiagEval[rtolDiagIndex];
        rowBasedEval[rowIndex] = rowHeuristics - rowAntiHeu;
        colBasedEval[colIndex] = colHeuristics - colAntiHeu;
        ltorDiagEval[ltorDiagIndex] = ltorHeuristics - ltorAntiHeu;
        rtolDiagEval[rtolDiagIndex] = rtolHeuristics - rtolAntiHeu;
        int curTotEval = rowBasedEval[rowIndex] + colBasedEval[colIndex] + ltorDiagEval[ltorDiagIndex]
                + rtolDiagEval[rtolDiagIndex];
        curHeuristic = curHeuristic - prevTotEval + curTotEval;
    }
	
	private int evaluateBoardPV(boolean first) {
		int curScore = 0;
		int[] kind = new int[]{0, 0};
		for (int i = 0; i < rowBased.length; i++) {
			curScore += evaluateLine(rowBased[i], width, first, kind);
		}
		
		for (int i = 0; i < colBased.length; i++) {
			curScore += evaluateLine(colBased[i], height, first, kind);
		}
		
		for (int i = 0; i < ltorDiag.length; i++) {
			curScore += evaluateLine(ltorDiag[i], Math.min(i + 1, width + height - 1 - i), first, kind);
		}
		
		for (int i = 0; i < rtolDiag.length; i++) {
			curScore += evaluateLine(rtolDiag[i], Math.min(i + 1, width + height - 1 - i), first, kind);
		}
		
		if (curScore >= winning_score)
			return winning_score;
        else if (kind[1] >= 2)
            return four_four;
        else if (kind[1] >= 1 && kind[0] >= 1)
            return three_four;
        else if (kind[0] >= 2)
            return three_three;
		
		return curScore;
	}

    /**
     * CORE FUNCTION
     * Get current heuristics, taking into consideration of composite patterns
     * @return
     */
	public int getHeuristics() {
        int whiteOffset = 0;
        int blackOffset = 0;
        if (totWhiteFour >= 2) {
            whiteOffset = four_four;
        } else if (totWhiteFour >= 1 && totWhiteThree >= 1) {
            whiteOffset = three_four;
        } else if (totWhiteThree >= 2) {
            whiteOffset = three_three;
        }

        if (totBlackFour >= 2) {
            blackOffset = four_four;
        } else if (totBlackFour >= 1 && totBlackThree >= 1) {
            blackOffset = three_four;
        } else if (totBlackThree >= 2) {
            blackOffset = three_three;
        }

        return curHeuristic - whiteOffset + blackOffset;
    }

    /**
     * Get increment in heuristics after move is played
     * @param move move to play
     * @param first whether or not black plays the move
     * @return increment in heuristics
     */
	public int getInc(int move, boolean first) {
        int prevHeu = getHeuristics();
		updateBoard(move, first);
		int postHeu = getHeuristics();
		withdrawMove(move);
		
		return first ? postHeu - prevHeu : prevHeu - postHeu;
	}

	private static int evaluateLineNoBrainer(int line, int numPos, boolean first) {
	    if (numPos < 5)
	        return 0;

	    String base4Str = Integer.toString(line, 4);
        while (base4Str.length() < numPos) {
            base4Str = '0' + base4Str;
        }

        int curScore = 0;
        String patOpenFour = first ? "033330" : "022220";
        String patSpecial = first ? "3033303" : "2022202";
        String patJumpFour1 = first ? "33033" : "22022";
        String patJumpFour2 = first ? "30333" : "20222";
        String patJumpFour3 = first ? "33303" : "22202";
        String patOJumpFour1 = first ? "303330" : "202220";
        String patOJumpFour2 = first ? "033303" : "022202";
        String patClosedFour1 = first ? "33033" : "22022";
        String patClosedFour2 = first ? "233330" : "322220";
        String patClosedFour3 = first ? "033332" : "022223";
        String patClosedFour4 = first ? "233303" : "322202";
        String patClosedFour5 = first ? "303332" : "202223";
        String patClosedFourEnd = first ? "03333" : "02222";
        String patClosedFourStart = first ? "33330" : "22220";
        String patOpenThree1 = first ? "003330" : "002220";
        String patOpenThree2 = first ? "033300" : "022200";
        String patJumpThree1 = first ? "033030" : "022020";
        String patJumpThree2 = first ? "030330" : "020220";

        if (base4Str.contains(patOpenFour) || base4Str.contains(patSpecial))
            return open_four;

        // For open/jump three's and four's -- no double counting (impossible in real games)
        if (base4Str.contains(patOJumpFour1) || base4Str.contains(patOJumpFour2))
            curScore += ojump_four;
        else if (base4Str.contains(patJumpFour1) || base4Str.contains(patJumpFour2) ||
                base4Str.contains(patJumpFour3))
            curScore += jump_four;
        else if (base4Str.contains(patClosedFour1) || base4Str.contains(patClosedFour2)
                || base4Str.contains(patClosedFour3) || base4Str.contains(patClosedFour4)
                || base4Str.contains(patClosedFour5) || base4Str.startsWith(patClosedFourStart)
                || base4Str.endsWith(patClosedFourEnd))
            curScore += closed_four;
        else if (base4Str.contains(patOpenThree1) || base4Str.contains(patOpenThree2))
            curScore += open_three;
        else if (base4Str.contains(patJumpThree1) || base4Str.contains(patJumpThree2))
            curScore += jump_three;
	    return curScore;
    }

    private static int evaluateLine(int line, int numPos, boolean first, int[] criticalKind) {
	    return evaluateLine(line, numPos, first, criticalKind, true);
    }

    /**
     * Get line heuristics as well as number of three/fours
     * @param line the line to evaluate
     * @param numPos number of positions in the line
     * @param first whether or not the subject is black
     * @param criticalKind must be int[2], first entry stores # of threes, and second stores # of fours
     * @return heuristic of the line
     */
	public static int evaluateLine(int line, int numPos, boolean first, int[] criticalKind, boolean confidence) {
		// TODO corner cases: pattern at the ends
		// If less than 5 positions, no value
		if (numPos < 5)
			return 0;

		totalEvals++;

		// Long-existing bug: If the line hits the cache, then there's no way to tell its critical kinds!!!
        // TODO this should really bring up my attention
		Integer cached = first ? evalMapsBlack.get(numPos).get(line) :
			evalMapsWhite.get(numPos).get(line);
		if (cached != null) {
		    byte entry = first ? criticalMapsBlack.get(numPos).get(line) : criticalMapsWhite.get(numPos).get(line);
		    criticalKind[0] = entry % 4;
		    criticalKind[1] = entry / 4;
		    cachedEvals++;
            return cached;
        } else if (confidence) {
            return 0;
        }
		
		String base4Str = Integer.toString(line, 4);
		while (base4Str.length() < numPos) {
			base4Str = '0' + base4Str;
		}

		// Consider borders as walls or opponent pieces, so corner cases are solved
		if (first) {
		    base4Str = '2' + base4Str + '2';
        } else {
		    base4Str = '3' + base4Str + '3';
        }
		
		int curScore = 0;
		String patFive = first ? "33333" : "22222";
		String patOpenFour = first ? "033330" : "022220";
		String patSpecial = first ? "3033303" : "2022202";
		String patJumpFour1 = first ? "33033" : "22022";
		String patJumpFour2 = first ? "30333" : "20222";
		String patJumpFour3 = first ? "33303" : "22202";
		String patOJumpFour1 = first ? "303330" : "202220";
		String patOJumpFour2 = first ? "033303" : "022202";
		String patClosedFour1 = first ? "33033" : "22022";
		String patClosedFour2 = first ? "233330" : "322220";
		String patClosedFour3 = first ? "033332" : "022223";
		String patClosedFour4 = first ? "233303" : "322202";
		String patClosedFour5 = first ? "303332" : "202223";
		String patOpenThree1 = first ? "003330" : "002220";
		String patOpenThree2 = first ? "033300" : "022200";
		String patJumpThree1 = first ? "033030" : "022020";
		String patJumpThree2 = first ? "030330" : "020220";
		
//		// TODO consider closed jump 3
		String patClosedThree1 = first ? "003332" : "002223";
		String patClosedThree2 = first ? "233300" : "322200";
		String patClosedThreeEnd = first ? "00333" : "00222";
		String patClosedThreeStart = first ? "33300" : "22200";
		String patClosedThreesp = first ? "2033302" : "3022203";
		String patClosedThreespStart = first ? "033302" : "022203";
		String patClosedJumpThree = first ? "233030" : "322020";
		String patClosedJumpThree2 = first ? "030332" : "020223";
		String patClosedJumpThree3 = first ? "230330" : "320220";
		String patClosedJumpThree4 = first ? "033032" : "022023";
		String patClosedJumpThreeStart = first ? "33030" : "22020";
		String patOpenTwo = first ? "003300" : "002200";
		String patSJTwo = first ? "0030300" : "0020200";
		String patBJTwo = first ? "030030" : "020020";
		
		// If already won or will be winning, return corresponding scores
		// "X0XXX0X" is a special kind, which counts as an open four
		if (base4Str.contains(patFive))
			return winning_score;
		if (base4Str.contains(patOpenFour) || base4Str.contains(patSpecial))
		    // Here the four is not counted, since the value is already recognized
			return open_four;

        /**
         * TODO note that this isn't necessarily accurate since theoretically there could be two fours/threes of the same pattern on the same line.
         * But this is pointless in real games since that's unlikely to happen
         */
		// For open/jump three's and four's -- no double counting (impossible in real games)
		if (base4Str.contains(patOJumpFour1)) {
            curScore += ojump_four;
            criticalKind[1]++;
        }
        else if (base4Str.contains(patOJumpFour2)) {
		    // Possible to create two open jump fours in one move
		    curScore += ojump_four;
		    criticalKind[1]++;
        }
		else if (base4Str.contains(patJumpFour1) || base4Str.contains(patJumpFour2) ||
				base4Str.contains(patJumpFour3)) {
            curScore += jump_four;
            criticalKind[1]++;
        }
		else if (base4Str.contains(patClosedFour1) || base4Str.contains(patClosedFour2)
				|| base4Str.contains(patClosedFour3) || base4Str.contains(patClosedFour4)
				|| base4Str.contains(patClosedFour5)) {
            curScore += closed_four;
            criticalKind[1]++;
        }
		else if (base4Str.contains(patOpenThree1) || base4Str.contains(patOpenThree2)) {
            curScore += open_three;
            criticalKind[0]++;
        }
        // Note that it's not impossible to have two jump_three's. E.g. xx_x_xx but that's effectively one cjump_three
        // since it's not a double threat (blockable with one piece)
		else if (base4Str.contains(patJumpThree1) || base4Str.contains(patJumpThree2)) {
            curScore += jump_three;
            criticalKind[0]++;
        }
		
		int closedThreeCount = 0, startPos = 0;
		if (base4Str.startsWith(patClosedThreeStart) || base4Str.endsWith(patClosedThreeEnd))
			closedThreeCount ++;
		while ((startPos = base4Str.indexOf(patClosedThree1, startPos)) != -1) {
			startPos ++;
			closedThreeCount ++;
		}
		
		startPos = 0;
		while ((startPos = base4Str.indexOf(patClosedThree2, startPos)) != -1) {
			startPos ++;
			closedThreeCount ++;
		}
		curScore += closedThreeCount * closed_three;

		if (base4Str.contains(patClosedJumpThree) || base4Str.contains(patClosedJumpThree2)
				|| base4Str.contains(patClosedJumpThree3) || base4Str.contains(patClosedJumpThree4)
				|| base4Str.startsWith(patClosedJumpThreeStart))
			curScore += cjump_three;
		
		if (base4Str.contains(patClosedThreesp) || base4Str.startsWith(patClosedThreespStart))
			curScore += closed_three;
		startPos = 0;
		int openTwoCount = 0;
		// TODO revise the definition of "open two"
		while ((startPos = base4Str.indexOf(patOpenTwo, startPos)) != -1) {
			startPos ++;
			openTwoCount ++;
		}
		
		startPos = 0;
		int smallJumpTwoCount = 0;
		while ((startPos = base4Str.indexOf(patSJTwo, startPos)) != -1) {
			startPos ++;
			smallJumpTwoCount ++;
		}
		
		startPos = 0;
		int bigJumpTwoCount = 0;
		while ((startPos = base4Str.indexOf(patBJTwo, startPos)) != -1) {
			startPos ++;
			bigJumpTwoCount ++;
		}
		
		curScore += openTwoCount * open_two + smallJumpTwoCount * small_jump_two
				+ bigJumpTwoCount * big_jump_two;
		
		if (first) {
            evalMapsBlack.get(numPos).put(line, curScore);
            criticalMapsBlack.get(numPos).put(line, (byte) (criticalKind[0] + criticalKind[1] * 4));
        }
		else {
            evalMapsWhite.get(numPos).put(line, curScore);
            criticalMapsWhite.get(numPos).put(line, (byte) (criticalKind[0] + criticalKind[1] * 4));
        }
		return curScore;
	}
	
	private boolean isSquareEmpty(int position) {
		return position >= 0 && position < width * height && 
				((rowBased[position / width]) >> (position % width * 2)) % 4 == 0;
	}
	
	private void genAdjMovesReduced() {
		for (int position = 0; position < width*height; position++) {
			List<Integer> returnVal = new ArrayList<>();
			int rowNum = position / width;
			int colNum = position % width;
			List<Integer> xcoords = Arrays.asList(colNum, colNum-1, colNum+1);
			List<Integer> ycoords = Arrays.asList(rowNum, rowNum-1, rowNum+1);
			for (int r : ycoords) {
				if (r >= height || r < 0)
					continue;
				for (int c : xcoords) {
					if (c >= width || c < 0)
						continue;
					if ((c == colNum && r == rowNum))
						continue;
					returnVal.add(r * width + c);
				}
			}
			
			adjacentMapRed.put(position, returnVal);
		}
	}
	
	private void generateAdjacentMoves() {
		for (int position = 0; position < width*height; position++) {
			List<Integer> returnVal = new ArrayList<>();
			int rowNum = position / width;
			int colNum = position % width;
			List<Integer> xcoords = Arrays.asList(colNum, colNum-1, colNum-2, colNum+1, colNum+2);
			List<Integer> ycoords = Arrays.asList(rowNum, rowNum-1, rowNum-2, rowNum+1, rowNum+2);
			for (int r : ycoords) {
				if (r >= height || r < 0)
					continue;
				for (int c : xcoords) {
					if (c >= width || c < 0)
						continue;
					if ((Math.abs(r - rowNum) == 1 && Math.abs(c - colNum) == 2)
							|| (Math.abs(r - rowNum) == 2 && Math.abs(c - colNum) == 1)
							|| (c == colNum && r == rowNum))
						continue;
					returnVal.add(r * width + c);
				}
			}
			
			adjacentMap.put(position, returnVal);
		}
	}
	
	public Set<Integer> nextMoves() {
		List<Integer> allStones = allStonesOnBoard();
		Set<Integer> ret = new HashSet<>();
		for (int stone : allStones) {
			for (int adj : adjacentMap.get(stone)) {
				if (isSquareEmpty(adj))
					ret.add(adj);
			}
		}
		
		return ret;
	}

	public Set<Integer> nextMovesRed() {
        List<Integer> allStones = allStonesOnBoard();
        Set<Integer> ret = new HashSet<>();
        for (int stone : allStones) {
            for (int adj : adjacentMapRed.get(stone)) {
                if (isSquareEmpty(adj))
                    ret.add(adj);
            }
        }

        return ret;
    }
	
	private List<Integer> allStonesOnBoard() {
		List<Integer> returnVal = new ArrayList<>();
		for (int i = 0; i < rowBased.length; i++) {
			String b4s = Integer.toString(rowBased[i], 4);
			for (int j = 0; j < b4s.length(); j++) {
				if (b4s.charAt(j) != '0')
					returnVal.add(i*width + b4s.length() - 1 - j);
			}
		}
		
		return returnVal;
	}
	
	private int getltorDiagIndex(int position) {
		int rowIndex = position / width;
		int colIndex = position % width;
		return rowIndex - colIndex + width - 1;
	}
	
	private int getrtolDiagIndex(int position) {
		int rowIndex = position / width;
		int colIndex = position % width;
		return rowIndex + colIndex;
	}
	
	private int getIndexOnLtoR(int position) {
		int rowIndex = position / width;
		int colIndex = position % width;
		int ltorIdx = getltorDiagIndex(position);
		return ltorIdx < width ? rowIndex : colIndex;
	}
	
	private int getIndexOnRtoL(int position) {
		int rowIndex = position / width;
		int colIndex = position % width;
		int rtolIdx = getrtolDiagIndex(position);
		return rtolIdx < width ? rowIndex : width - 1 - colIndex;
	}

    private int lrDiagToBoardPosition(int lrIndex, int indexOnLR) {
		if (lrIndex < width) {
			int rowIndex = indexOnLR;
			int colIndex = (width - 1 - lrIndex) + indexOnLR;
			return rowIndex * width + colIndex;
		} else {
			int rowIndex = (lrIndex - width + 1) + indexOnLR;
			int colIndex = indexOnLR;
			return rowIndex * width + colIndex;
		}
	}

    private int rlDiagToBoardPosition(int rlIndex, int indexOnRL) {
		if (rlIndex < width) {
			int rowIndex = indexOnRL;
			int colIndex = rlIndex - indexOnRL;
			return rowIndex * width + colIndex;
		} else {
			int rowIndex = (rlIndex - width + 1) + indexOnRL;
			int colIndex = width - 1 - indexOnRL;
			return rowIndex * width + colIndex;
		}
	}

    /**
     * CORE FUNCTION
     * Withdraws the given move
     * PRE-CONDITION: move is already on the board
     * @param move
     */
	public void withdrawMove(int move) {
		if (move < 0 || move >= width * height)
			return;
		int rowIndex = move / width;
		int colIndex = move % width;
		int ltorIdx = getltorDiagIndex(move);
		int rtolIdx = getrtolDiagIndex(move);
		int indexOnLtoRDiag = ltorIdx < width ? rowIndex : colIndex;
		int indexOnRtoLDiag = rtolIdx < width ? rowIndex : width - 1 - colIndex;
		int stone = rowBased[rowIndex] & (3 << (colIndex * 2));
		// Restore the zobrist hash.
//		if (stone == sente_stone) {
//		    zobristHash = Zobrist.zobristHash(move, true, zobristHash);
//        } else if (stone == gote_stone) {
//            zobristHash = Zobrist.zobristHash(move, false, zobristHash);
//        }
		rowBased[rowIndex] &= (-1 - (3 << (colIndex * 2)));
		colBased[colIndex] &= (-1 - (3 << (rowIndex * 2)));
		rtolDiag[rtolIdx] &= (-1 - (3 << (indexOnRtoLDiag * 2)));
		ltorDiag[ltorIdx] &= (-1 - (3 << (indexOnLtoRDiag * 2)));

		// After proper withdrawal, update heuristics as well
        updateHeuristics(move);
	}
	
	public boolean checkWinningLite(int move, boolean first) {
		String pattern = first ? "33333" : "22222";
		int rowIdx = move / width;
		int colIdx = move % width;
		int ltorIdx = getltorDiagIndex(move);
		int rtolIdx = getrtolDiagIndex(move);
		String row = Integer.toString(rowBased[rowIdx], 4);
		String col = Integer.toString(colBased[colIdx], 4);
		String lrDiag = Integer.toString(ltorDiag[ltorIdx], 4);
		String rlDiag = Integer.toString(rtolDiag[rtolIdx], 4);
		
		if (row.contains(pattern))
			return true;
		if (col.contains(pattern))
			return true;
		if (lrDiag.contains(pattern))
			return true;
		if (rlDiag.contains(pattern))
			return true;

		return false;
	}
	
	public void writeRecordToFile() {
		long curTime = System.currentTimeMillis();
		String fileName = "C:\\Users\\wuxiujuan\\Documents\\Gomoku\\" + curTime + ".txt";
		List<Integer> blackStones = new ArrayList<>();
		List<Integer> whiteStones = new ArrayList<>();
		for (int i = 0; i < rowBased.length; i++) {
			int curRow = rowBased[i];
			for (int j = 0; j < width; j++) {
				int target = (curRow >> (2*j)) & 3;
				if (target == 3)
					blackStones.add(i * width + j);
				else if (target == 2)
					whiteStones.add(i * width + j);
			}
		}
		
		try {
			PrintWriter pr = new PrintWriter(fileName);
			String xString = "xcoord = Arrays.asList(";
			String yString = "ycoord = Arrays.asList(";
			for (int blackStone : blackStones) {
				xString += (blackStone % width) + ",";
				yString += (blackStone / width) + ",";
			}
			xString = xString.substring(0, xString.length() - 1) + ");";
			yString = yString.substring(0, yString.length() - 1) + ");";
			
			pr.println(xString);
			pr.println(yString);
			pr.println("updateBoardInBatch(bd, ycoord, xcoord, true);");
			
			xString = "xcoord = Arrays.asList(";
			yString = "ycoord = Arrays.asList(";
			for (int whiteStone : whiteStones) {
				xString += (whiteStone % width) + ",";
				yString += (whiteStone / width) + ",";
			}
			
			xString = xString.substring(0, xString.length() - 1) + ");";
			yString = yString.substring(0, yString.length() - 1) + ");";
			
			pr.println(xString);
			pr.println(yString);
			pr.println("updateBoardInBatch(bd, ycoord, xcoord, false);");
			
			pr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the square to win if first can win in one move if he plays next;
	 * if the first doesn't have a direct threat, return a negative number.
	 * Notice that the return value is also the blocking square of the opponent.
	 * @param first - black to move or white
	 * @return
	 */
	public int canWinNextMove(boolean first) {
		// TODO return the exact location
		for (int i = 0; i < height; i++) {
			int row = rowBased[i];
			int fourResult = lineHasFour(row, width, first);
			if (fourResult >= 0)
				return i * width + fourResult;
		}
		
		for (int i = 0; i < width; i++) {
			int col = colBased[i];
			int fourResult = lineHasFour(col, height, first);
			if (fourResult >= 0)
				return fourResult * width + i;
		}
		
		for (int i = 0; i < ltorDiag.length; i++) {
			int fourResult = lineHasFour(ltorDiag[i], Math.min(i + 1, width + height - 1 - i), first);
			if (fourResult >= 0)
				return lrDiagToBoardPosition(i, fourResult);
		}
		
		for (int i = 0; i < rtolDiag.length; i++) {
			int fourResult = lineHasFour(rtolDiag[i], Math.min(i + 1, width + height - 1 - i), first);
			if (fourResult >= 0)
				return rlDiagToBoardPosition(i, fourResult);
		}
		
		return -3;
	}
	
	/**
	 * Return index into the line of the empty location if four found; 
	 * otherwise return a negative number indicating no four was found.
	 * @param line
	 * @param numPos
	 * @param first
	 * @return
	 */
	private int lineHasFour(int line, int numPos, boolean first) {
		if (numPos < 5)
			return -2;
		
		StringBuffer sb = getBase4Str(line, numPos);

		char selfChar = first ? '3' : '2';
		int selfCount = 0, emptyCount = 0;
		for (int i = 0; i < 5; i++) {
			if (sb.charAt(i) == selfChar)
				selfCount++;
			else if (sb.charAt(i) == '0')
				emptyCount++;
		}
		
		if (selfCount >= 4 && emptyCount >= 1) {
			for (int i = 0; i < 5; i++) {
				if (sb.charAt(i) == '0')
					return i;
			}
		}
		
		for (int i = 0; i + 5 < sb.length(); i++) {
			char backChar = sb.charAt(i);
			char frontChar = sb.charAt(i + 5);
			if (backChar == selfChar)
				selfCount--;
			else if (backChar == '0')
				emptyCount--;
			
			if (frontChar == selfChar)
				selfCount++;
			else if (frontChar == '0')
				emptyCount++;
			
			if (selfCount >= 4 && emptyCount >= 1)
				for (int j = i + 1; j <= i + 5; j++) {
					if (sb.charAt(j) == '0')
						return j;
				}
		}
		
		return -2;
	}
	
	private StringBuffer getBase4Str(int line, int numPos) {
		StringBuffer sb = new StringBuffer(Integer.toString(line, 4)).reverse();
		while (sb.length() < numPos) {
			sb.append('0');
		}
		return sb;
	}
	
	/**
	 * Check whether the last move made by the opponent forms a four,
	 * in which case we have to take immediate action.
	 * @param first
	 * @param lastMove
	 * @param blocking where the blocking location is stored if function returns true
	 * @return
	 */
	public boolean formedThreat(boolean first, int lastMove, int[] blocking) {
		int rowIndex = lastMove / width;
		int colIndex = lastMove % width;
		int lrIdx = getltorDiagIndex(lastMove);
		int rlIdx = getrtolDiagIndex(lastMove);
		int rowRes = lineHasFour(rowBased[rowIndex], width, first);
		if (rowRes >= 0) {
			blocking[0] = rowIndex * width + rowRes;
			return true;
		}
		
		int colRes = lineHasFour(colBased[colIndex], height, first);
		if (colRes >= 0) {
			blocking[0] = colRes * width + colIndex;
			return true;
		}
		
		int lrRes = lineHasFour(ltorDiag[lrIdx], Math.min(lrIdx + 1, width + height - lrIdx - 1), first);
		if (lrRes >= 0) {
			blocking[0] = lrDiagToBoardPosition(lrIdx, lrRes);
			return true;
		}
		
		int rlRes = lineHasFour(rtolDiag[rlIdx], Math.min(rlIdx + 1, width + height - rlIdx - 1), first);
		if (rlRes >= 0) {
			blocking[0] = rlDiagToBoardPosition(rlIdx, rlRes);
			return true;
		}
		
		blocking[0] = -1;
		return false;
	}
	
	/**
	 * Find all blocking locations of three's of the given player.
	 * These locations do not guarantee to be effective, but at least
	 * it helps reduce branching factor.
	 * Pre-condition: the player to check does not have any four's
	 * @param first
	 * @return
	 */
	public Set<Integer> findAllThrees(boolean first) {
	    // TODO BUGGY FUNCTION!!!
        // TODO after debugging please don't iterate through all lines, use the last move please!
        // TODO actually this is hard to do without framework support, but now that we have the framework, it's basically just caching more stuffs besides which lines
        // TODO having threes or fours
		Set<Integer> retVal = new HashSet<>();
		for (int i = 0; i < rowBased.length; i++) {
			Set<Integer> res = findThree(getBase4Str(rowBased[i], width), first);
			for (int r : res) {
				retVal.add(i * width + r);
			}
		}
		
		for (int i = 0; i < colBased.length; i++) {
			Set<Integer> res = findThree(getBase4Str(colBased[i], height), first);
			for (int r : res) {
				retVal.add(r * width + i);
			}
		}
		
		for (int i = 0; i < ltorDiag.length; i++) {
			Set<Integer> res = findThree(getBase4Str(ltorDiag[i], Math.min(i + 1, width + height - 1 - i)), first);
			for (int r : res) {
				retVal.add(lrDiagToBoardPosition(i, r));
			}
		}
		
		for (int i = 0; i < rtolDiag.length; i++) {
			Set<Integer> res = findThree(getBase4Str(rtolDiag[i], Math.min(i + 1, width + height - 1 - i)), first);
			for (int r : res) {
				retVal.add(rlDiagToBoardPosition(i, r));
			}
		}
		
		return retVal;
	}
	
	/**
	 * Return the index into the line where threes can be blocked.
	 * If no three is found, return empty list.
	 * @param line
	 * @param first
	 * @return
	 */
	private Set<Integer> findThree(StringBuffer line, boolean first) {
		Set<Integer> retVal = new HashSet<>();
		if (line.length() <= 5)
			return retVal;
		
		int selfCnt = 0, empCnt = 0;
		char selfCh = first ? '3' : '2';
		char oppCh = first ? '2' : '3';
		char empty = '0';

		// Keep track of the set of empty locations
		Set<Integer> empLocSet = new HashSet<>();
		for (int i = 0; i < 6; i++) {
			char ch = line.charAt(i);
			if (ch == selfCh)
				selfCnt ++;
			else if (ch == empty) {
				empLocSet.add(i);
				empCnt ++;
			}
		}
		
		if (selfCnt == 3 && empCnt == 3 && empLocSet.contains(0) && empLocSet.contains(5)) {
			if (empLocSet.contains(1)) {
				retVal.add(1);
				retVal.add(5);
			} else {
				retVal.addAll(empLocSet);
			}
		}
		
		for (int i = 0, j = 6; j < line.length(); i++, j++) {
			char front = line.charAt(j);
			char back = line.charAt(i);
			if (back == selfCh)
				selfCnt --;
			else if (back == empty) {
				empCnt --;
				empLocSet.remove(i);
			}
			
			if (front == selfCh)
				selfCnt ++;
			else if (front == empty) {
				empCnt ++;
				empLocSet.add(j);
			}

			// Two ends empty; meets criteria
			if (selfCnt == 3 && empCnt == 3 && empLocSet.contains(i + 1) && empLocSet.contains(j)) {
				// First check the boundary
				if (j == line.length() - 1) {
					if (empLocSet.contains(j - 1)) {
						retVal.add(i + 1);
						retVal.add(j - 1);
					} else retVal.addAll(empLocSet);
				} else if (line.charAt(i) == oppCh) { // blocked in the front
					if (line.charAt(j + 1) == oppCh) {
						// two sides blocked, add all;
						retVal.addAll(empLocSet);
					} else if (empLocSet.contains(i + 2)) {
						retVal.add(i + 2);
						retVal.add(j);
					} else if (empLocSet.contains(j - 1)) {
					    retVal.addAll(empLocSet);
                    } else retVal.addAll(empLocSet);
				} else if (line.charAt(j + 1) == oppCh) { // blocked in the back but open in the front
					if (empLocSet.contains(j - 1)) {
						retVal.add(j - 1);
						retVal.add(i + 1);
					} else {
						retVal.addAll(empLocSet);
					}
				} else { // open on both sides
					if (empLocSet.contains(i + 2)) {
						retVal.add(i + 2);
						retVal.add(j);
					} else if (empLocSet.contains(j - 1)) {
						retVal.add(j - 1);
						retVal.add(i + 1);
					} else retVal.addAll(empLocSet);
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Check if the last move made by opponent forms a three, in which case
	 * we have to either block or use global refutation (i.e. form a four).
	 * @param first
	 * @param lastMove
	 * @return
	 */
	public boolean formedThree(boolean first, int lastMove) {
		char selfCh = first ? '3' : '2';
		int rowIndex = lastMove / width;
		int colIndex = lastMove % width;
		int lrIdx = getltorDiagIndex(lastMove);
		int rlIdx = getrtolDiagIndex(lastMove);
		int idxOnLR = getIndexOnLtoR(lastMove);
		int idxOnRL = getIndexOnRtoL(lastMove);
		StringBuffer row = getBase4Str(rowBased[rowIndex], width);
		if (formedThree(row, selfCh, colIndex))
			return true;
		
		StringBuffer col = getBase4Str(colBased[colIndex], width);
		if (formedThree(col, selfCh, rowIndex))
			return true;
		
		StringBuffer lrDiag = getBase4Str(ltorDiag[lrIdx], Math.min(lrIdx + 1, width + height - 1 - lrIdx));
		if (formedThree(lrDiag, selfCh, idxOnLR))
			return true;
		
		StringBuffer rlDiag = getBase4Str(rtolDiag[rlIdx], Math.min(rlIdx + 1, width + height - 1 - rlIdx));
		if (formedThree(rlDiag, selfCh, idxOnRL))
			return true;
		
		return false;
	}
	
	/**
	 * Helper of the above function (with the same name).
	 * @param line
	 * @param selfCh
	 * @param indexOnLine
	 * @return
	 */
	private boolean formedThree(StringBuffer line, char selfCh, int indexOnLine) {
		int idxStart = Math.max(0, indexOnLine - 4);
		if (idxStart + 6 > line.length())
			return false;
		
		int selfCnt = 0, empCnt = 0;
		for (int i = idxStart; i < idxStart + 6; i++) {
			char ch = line.charAt(i);
			if (ch == selfCh) 
				selfCnt ++;
			else if (ch == '0')
				empCnt ++;
		}
		
		if (selfCnt == 3 && empCnt == 3 && line.charAt(idxStart) == '0' &&
				line.charAt(idxStart + 5) == '0')
			return true;
		
		int startCtr = idxStart, endCtr = idxStart + 6;
		for (; endCtr < Math.min(indexOnLine + 5, line.length()); startCtr++, endCtr++) {
			char startCh = line.charAt(startCtr);
			char endCh = line.charAt(endCtr);
			if (startCh == selfCh)
				selfCnt --;
			else if (startCh == '0')
				empCnt --;
			
			if (endCh == selfCh)
				selfCh ++;
			else if (endCh == '0')
				empCnt ++;
			
			if (selfCnt == 3 && empCnt == 3 && line.charAt(startCtr + 1) == '0'
					&& endCh == '0')
				return true;
		}
		
		return false;
	}

    /**
     * Find places to form a four
     * @param first
     * @return
     */
	public Map<Integer, Integer> findThreatLocation(boolean first) {
		List<Integer> selfStones = allSelfStones(first);
		Map<Integer, Integer> returnVal = new HashMap<>();
		Set<Integer> possibleLocs = new HashSet<>();
		for (int stone : selfStones) {
			for (int adj : adjacentMapRed.get(stone)) {
				if (isSquareEmpty(adj))
					possibleLocs.add(adj);
			}
		}
		
		for (int loc : possibleLocs) {
			updateBoard(loc, first);
			int ri = loc / width;
			int ci = loc % width;
			int lri = getltorDiagIndex(loc);
			int rli = getrtolDiagIndex(loc);
			int rowRes = lineHasFour(rowBased[ri], width, first);
			if (rowRes >= 0) {
				returnVal.put(loc, ri * width + rowRes);
				withdrawMove(loc);
				continue;
			}
			
			int colRes = lineHasFour(colBased[ci], height, first);
			if (colRes >= 0) {
				returnVal.put(loc, colRes * width + ci);
				withdrawMove(loc);
				continue;
			}
			
			int lrDiagRes = lineHasFour(ltorDiag[lri], Math.min(lri + 1, 
					width + height - lri - 1), first);
			if (lrDiagRes >= 0) {
				returnVal.put(loc, lrDiagToBoardPosition(lri, lrDiagRes));
				withdrawMove(loc);
				continue;
			}
			
			int rlDiagRes = lineHasFour(rtolDiag[rli], Math.min(rli + 1, 
					width + height - rli - 1), first);
			if (rlDiagRes >= 0)
				returnVal.put(loc, rlDiagToBoardPosition(rli, rlDiagRes));
			withdrawMove(loc);
		}
		
		return returnVal;
	}
	
	public List<Integer> allSelfStones(boolean first) {
		char stone = first ? '3' : '2';
		List<Integer> returnVal = new ArrayList<>();
		for (int i = 0; i < rowBased.length; i++) {
			String b4s = Integer.toString(rowBased[i], 4);
			for (int j = 0; j < b4s.length(); j++) {
				if (b4s.charAt(j) == stone)
					returnVal.add(i*width + b4s.length() - 1 - j);
			}
		}
		
		return returnVal;
	}
	
	public abstract boolean someoneWins();
	
	public void addMoveToSequence(int move) {
		moveSequence.add(move);
	}
	
	public int getMostRecentMove() {
		return moveSequence.isEmpty() ? -1 : moveSequence.get(moveSequence.size() - 1);
	}
	
	public int getStoneCount() {
		return moveSequence.size();
	}
	
	public boolean boardFull() {
		for (int row : rowBased) {
			if ((row & 715827882) != 715827882)
				return false;
		}
		
		return true;
	}
	
	public int getFirstRandomMove() {
		int randomX = new Random().nextInt(3);
		int randomY = new Random().nextInt(3);
		return (6 + randomY) * width + (6 + randomX);
	}

	public void writeRecords(int gameResult) {
        try {
            LocalStorage.updateGameRecord(moveSequence, gameResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getMoveSequence() {
        return moveSequence;
    }

    public long getZobristHash() {
	    return zobristHash;
    }

    public void updateHash(int location, boolean first) {
        zobristHash = Zobrist.zobristHash(location, first, zobristHash);
    }
}
