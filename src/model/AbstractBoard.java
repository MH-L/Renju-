package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class AbstractBoard {
	protected static final int width = 15;
	protected static final int height = 15;
	protected static final int invalid_location = 225;
	protected static final int sente_stone = 3;
	protected static final int gote_stone = 2;
	
	protected static final int winning_score = 1000000;
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
	
	protected static final int has_three = 3;
	protected static final int has_four = 4;
	protected static final int has_none = 0;
	
	protected int[] rowBased;
	protected int[] colBased;
	protected int[] ltorDiag;
	protected int[] rtolDiag;
	protected int lastMove = invalid_location;
	private boolean activated = false;
	
	protected Map<Integer, List<Integer>> adjacentMap;
	
	public AbstractBoard() {
		rowBased = new int[height];
		colBased = new int[width];
		ltorDiag = new int[width + height - 1];
		rtolDiag = new int[width + height - 1];
		adjacentMap = new HashMap<>();
		genAdjMovesReduced();
	}
	
	/**
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
	
	public int evaluateBoard() {
		return evaluateBoardPV(true) - evaluateBoardPV(false);
	}
	
	public int evaluateBoardPV(boolean first) {
		int curScore = 0;
		// hasnone - 0, has3 - 1, has4 - 2; has33 - 3
		// has34 or has44 - 4
		int curStatus = 0;
		int[] kind = new int[]{0};
		for (int i = 0; i < rowBased.length; i++) {
			curScore += evaluateLine(rowBased[i], width, first, kind);
			if (curStatus == 0) {
				if (kind[0] == has_three)
					curStatus = 1;
				else if (kind[0] == has_four)
					curStatus = 2;
			} else if (curStatus == 1) {
				if (kind[0] == has_three)
					curStatus = 3;
				else if (kind[0] == has_four) {
					curStatus = 4;
				}
			} else if (curStatus == 2) {
				if (kind[0] != has_none)
					curStatus = 4;
			} else if (curStatus == 3) {
				if (kind[0] == has_four)
					curStatus = 4;
			}
		}
		
		for (int i = 0; i < colBased.length; i++) {
			curScore += evaluateLine(colBased[i], height, first, kind);
			if (curStatus == 0) {
				if (kind[0] == has_three)
					curStatus = 1;
				else if (kind[0] == has_four)
					curStatus = 2;
			} else if (curStatus == 1) {
				if (kind[0] == has_three)
					curStatus = 3;
				else if (kind[0] == has_four) {
					curStatus = 4;
				}
			} else if (curStatus == 2) {
				if (kind[0] != has_none)
					curStatus = 4;
			} else if (curStatus == 3) {
				if (kind[0] == has_four)
					curStatus = 4;
			}
		}
		
		for (int i = 0; i < ltorDiag.length; i++) {
			curScore += evaluateLine(ltorDiag[i], 
					Math.min(i + 1, width + height - 1 - i), first, kind);
			if (curStatus == 0) {
				if (kind[0] == has_three)
					curStatus = 1;
				else if (kind[0] == has_four)
					curStatus = 2;
			} else if (curStatus == 1) {
				if (kind[0] == has_three)
					curStatus = 3;
				else if (kind[0] == has_four) {
					curStatus = 4;
				}
			} else if (curStatus == 2) {
				if (kind[0] != has_none)
					curStatus = 4;
			} else if (curStatus == 3) {
				if (kind[0] == has_four)
					curStatus = 4;
			}
		}
		
		for (int i = 0; i < rtolDiag.length; i++) {
			curScore += evaluateLine(rtolDiag[i], 
					Math.min(i + 1, width + height - 1 - i), first, kind);
			if (curStatus == 0) {
				if (kind[0] == has_three)
					curStatus = 1;
				else if (kind[0] == has_four)
					curStatus = 2;
			} else if (curStatus == 1) {
				if (kind[0] == has_three)
					curStatus = 3;
				else if (kind[0] == has_four) {
					curStatus = 4;
				}
			} else if (curStatus == 2) {
				if (kind[0] != has_none)
					curStatus = 4;
			} else if (curStatus == 3) {
				if (kind[0] == has_four)
					curStatus = 4;
			}
		}
		
		if (curScore >= winning_score)
			return winning_score;

		if (curStatus == 3)
			return Math.max(curScore, three_three);
		if (curStatus == 4)
			return Math.max(curScore, four_four);
		
		return curScore;
	}
	
	public static int evaluateLine(int line, int numPos, boolean first, int[] criticalKind) {
		// TODO corner cases: pattern at the ends
		// If less than 5 positions, no value
		if (numPos < 5)
			return 0;
		
		if (true)
			return new Random().nextInt(21) - 10;
		
		String base4Str = Integer.toString(line, 4);
		while (base4Str.length() < numPos) {
			base4Str = '0' + base4Str;
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
		String patClosedFour2 = first ? "33033" : "22022";
		String patClosedFourEnd = first ? "03333" : "02222";
		String patClosedFourStart = first ? "33330" : "22220";
		String patOpenThree1 = first ? "003330" : "002220";
		String patOpenThree2 = first ? "033300" : "022200";
		String patJumpThree1 = first ? "033030" : "022020";
		String patJumpThree2 = first ? "030330" : "020220";
//		
//		// TODO consider closed jump 3
//		String patClosedThree1 = first ? "003332" : "002223";
//		String patClosedThree2 = first ? "233300" : "322200";
//		String patClosedThreeEnd = first ? "00333" : "00222";
//		String patClosedThreeStart = first ? "33300" : "22200";
//		String patOpenTwo = first ? "003300" : "002200";
//		String patSJTwo = first ? "0030300" : "0020200";
//		String patBJTwo = first ? "030030" : "020020";
		
		// If already won or will be winning, return corresponding scores
		// "X0XXX0X" is a special kind, which counts as an open four
		if (base4Str.contains(patFive))
			return winning_score;
		if (base4Str.contains(patOpenFour) || base4Str.contains(patSpecial))
			return open_four;
		
		// For open/jump three's and four's -- no double counting (impossible in real games)
		if (base4Str.contains(patOJumpFour1) || base4Str.contains(patOJumpFour2))
			curScore += ojump_four;
		else if (base4Str.contains(patJumpFour1) || base4Str.contains(patJumpFour2) ||
				base4Str.contains(patJumpFour3))
			curScore += jump_four;
		else if (base4Str.contains(patClosedFour1) || base4Str.contains(patClosedFour2)
				|| base4Str.startsWith(patClosedFourStart) ||
				base4Str.endsWith(patClosedFourEnd))
			curScore += closed_four;
		else if (base4Str.contains(patOpenThree1) || base4Str.contains(patOpenThree2))
			curScore += open_three;
		else if (base4Str.contains(patJumpThree1) || base4Str.contains(patJumpThree2))
			curScore += jump_three;
		
		// Record kind of critical pattern
		if (curScore >= 15)
			criticalKind[0] = has_four;
		else if (curScore > 0)
			criticalKind[0] = has_three;
		else
			criticalKind[0] = has_none;
		
//		int closedThreeCount = 0, startPos = 0;
//		if (base4Str.startsWith(patClosedThreeStart) || base4Str.endsWith(patClosedThreeEnd))
//			closedThreeCount ++;
//		while ((startPos = base4Str.indexOf(patClosedThree1, startPos)) != -1) {
//			startPos ++;
//			closedThreeCount ++;
//		}
//		
//		startPos = 0;
//		while ((startPos = base4Str.indexOf(patClosedThree2, startPos)) != -1) {
//			startPos ++;
//			closedThreeCount ++;
//		}
//		curScore += closedThreeCount * closed_three;
//		
//		startPos = 0;
//		int openTwoCount = 0;
//		// TODO revise the definition of "open two"
//		while ((startPos = base4Str.indexOf(patOpenTwo, startPos)) != -1) {
//			startPos ++;
//			openTwoCount ++;
//		}
//		
//		startPos = 0;
//		int smallJumpTwoCount = 0;
//		while ((startPos = base4Str.indexOf(patSJTwo, startPos)) != -1) {
//			startPos ++;
//			smallJumpTwoCount ++;
//		}
//		
//		startPos = 0;
//		int bigJumpTwoCount = 0;
//		while ((startPos = base4Str.indexOf(patBJTwo, startPos)) != -1) {
//			startPos ++;
//			bigJumpTwoCount ++;
//		}
//		
//		curScore += openTwoCount * open_two + smallJumpTwoCount * small_jump_two
//				+ bigJumpTwoCount * big_jump_two;
			
		return curScore;
	}
	
	public void activate() {
		activated = true;
	}
	
	public void freeze() {
		activated = false;
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
			
			adjacentMap.put(position, returnVal);
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
	
	public int getltorDiagIndex(int position) {
		int rowIndex = position / width;
		int colIndex = position % width;
		return rowIndex - colIndex + width - 1;
	}
	
	public int getrtolDiagIndex(int position) {
		int rowIndex = position / width;
		int colIndex = position % width;
		return rowIndex + colIndex;
	}
	
	public int getIndexOnLtoR(int position) {
		int rowIndex = position / width;
		int colIndex = position % width;
		int ltorIdx = getltorDiagIndex(position);
		return ltorIdx < width ? rowIndex : colIndex;
	}
	
	public int getIndexOnRtoL(int position) {
		int rowIndex = position / width;
		int colIndex = position % width;
		int rtolIdx = getrtolDiagIndex(position);
		return rtolIdx < width ? rowIndex : width - 1 - colIndex;
	}
	
	public void withdrawMove(int move) {
		// TODO buggy
		if (move < 0 || move >= width * height)
			return;
		int rowIndex = move / width;
		int colIndex = move % width;
		int ltorIdx = getltorDiagIndex(move);
		int rtolIdx = getrtolDiagIndex(move);
		int indexOnLtoRDiag = ltorIdx < width ? rowIndex : colIndex;
		int indexOnRtoLDiag = rtolIdx < width ? rowIndex : width - 1 - colIndex;
		rowBased[rowIndex] &= (-1 - (3 << (colIndex * 2)));
		colBased[colIndex] &= (-1 - (3 << (rowIndex * 2)));
		rtolDiag[rtolIdx] &= (-1 - (3 << (indexOnRtoLDiag * 2)));
		ltorDiag[ltorIdx] &= (-1 - (3 << (indexOnLtoRDiag * 2)));
	}
}
