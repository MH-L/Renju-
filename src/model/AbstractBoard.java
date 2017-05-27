package model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBoard {
	protected static final int width = 15;
	protected static final int height = 15;
	protected static final int invalid_location = 225;
	protected static final int sente_stone = 3;
	protected static final int gote_stone = 2;
	protected int[] rowBased;
	protected int[] colBased;
	protected int[] ltorDiag;
	protected int[] rtolDiag;
	protected List<Integer> blackStones;
	protected List<Integer> whiteStones;
	protected int lastMove = invalid_location;
	
	public AbstractBoard() {
		blackStones = new ArrayList<>();
		whiteStones = new ArrayList<>();
		rowBased = new int[height];
		colBased = new int[width];
		ltorDiag = new int[width + height - 1];
		rtolDiag = new int[width + height - 1];
	}
	
	public boolean withdrawLastMove() {
		if (blackStones.size() > whiteStones.size())
			blackStones.remove(blackStones.size() - 1);
		else if (whiteStones.size() > 0)
			whiteStones.remove(whiteStones.size() - 1);
		else
			return false;
		return true;
	}
	
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

		if ((rowIndex & (3 << (colIndex * 2))) != 0)
			return false;
		int stone = first ? sente_stone : gote_stone;

		origRow = origRow ^ (stone << colIndex * 2);
		rowBased[rowIndex] = origRow;

		origCol = origCol ^ (stone << rowIndex * 2);
		colBased[colIndex] = origCol;

		origLtoRDiag = origLtoRDiag ^ (stone << rtolIndex * 2);
		ltorDiag[ltorIndex] = origLtoRDiag;

		origRtoLDiag = origRtoLDiag ^ (stone << ltorIndex * 2);
		rtolDiag[rtolIndex] = origRtoLDiag;
		
		return true;
	}
}
