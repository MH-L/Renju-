package model;

public class UnrestrictedBoard extends AbstractBoard {
	public boolean someoneWins() {
		int modulo = 1024; // 4^5
		int moduli = 1023; // 4^5 - 1 (moduli for black)
		int moduli_ = 682; // moduli for white
		
		// Check columns
		for (int i = 0; i < width; i++) {
			int curCol = colBased[i];
			for (int j = 0; j <= width - 5; j++) {
				if (((curCol >> j) ^ moduli) % modulo == 0
						|| ((curCol >> j) ^ moduli_) % modulo == 0)
					return true;
			}
		}
		
		// Check rows
		for (int i = 0; i < height; i++) {
			int curRow = rowBased[i];
			for (int j = 0; j <= width - 5; j++) {
				if (((curRow >> j) ^ moduli) % modulo == 0
						|| ((curRow >> j) ^ moduli_) % modulo == 0)
					return true;
			}
		}
		
		// Right diagonals
		for (int i = 4; i < height + width - 5; i++) {
			int curDiag = rtolDiag[i];
			int numPos = Math.min(i + 1, height + width - 1 - i);
			for (int j = 0; j <= numPos - 5; j++) {
				if (((curDiag >> j) ^ moduli) % modulo == 0
						|| ((curDiag >> j) ^ moduli_) % modulo == 0)
					return true;
			}
		}
		
		// Left diagonals
		for (int i = 4; i < height + width - 5; i++) {
			int curDiag = ltorDiag[i];
			int numPos = Math.min(i + 1, height + width - 1 - i);
			for (int j = 0; j <= numPos - 5; j++) {
				if (((curDiag >> j) ^ moduli) % modulo == 0
						|| ((curDiag >> j) ^ moduli_) % modulo == 0)
					return true;
			}
		}
		return false;
	}
}
