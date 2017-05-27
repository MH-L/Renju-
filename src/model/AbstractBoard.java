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
	protected int lastMove = invalid_location;
	
	public AbstractBoard() {
		rowBased = new int[height];
		colBased = new int[width];
		ltorDiag = new int[width + height - 1];
		rtolDiag = new int[width + height - 1];
	}
	
	/**
	 * Update board with given location: 00 for empty location,
	 * 10 for white stone, 11 for black stone
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

		if ((origRow & (3 << (colIndex * 2))) != 0)
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
}
