package model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBoard {
	protected static final int width = 15;
	protected static final int height = 15;
	protected static final int invalid_location = 225;
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
	
	public boolean updateBoard(int location) {
		if (location < 0 || location >= invalid_location)
			return false;
		int rowIndex = location / width;
		int colIndex = location % width;
		int origRow = rowBased[rowIndex];
		int origCol = colBased[colIndex];
		int origLtoRDiag = ltorDiag[rowIndex - colIndex + width - 1];
		int origRtoLDiag = rtolDiag[rowIndex + colIndex];
		
		
		return false;
	}
}
