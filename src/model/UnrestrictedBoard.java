package model;

public class UnrestrictedBoard extends AbstractBoard {
	public boolean someoneWins() {
		int modulo = 1024; // 4^5
		int moduli = 1023; // 4^5 - 1 (moduli for black)
		int moduli_ = 682; // moduli for white
		
		// Check columns
		for (int i = 0; i < width; i++) {
			String base4Str = Integer.toString(colBased[i], 4);
			if (base4Str.contains("33333") || base4Str.contains("22222"))
				return true;
		}
		
		// Check rows
		for (int i = 0; i < height; i++) {
			String base4Str = Integer.toString(rowBased[i], 4);
			if (base4Str.contains("33333") || base4Str.contains("22222"))
				return true;
		}
		
		// Right diagonals
		for (int i = 4; i < height + width - 5; i++) {
			String base4Str = Integer.toString(rtolDiag[i], 4);
			if (base4Str.contains("33333") || base4Str.contains("22222"))
				return true;
		}
		
		// Left diagonals
		for (int i = 4; i < height + width - 5; i++) {
			String base4Str = Integer.toString(ltorDiag[i], 4);
			if (base4Str.contains("33333") || base4Str.contains("22222"))
				return true;
		}
		return false;
	}
}
