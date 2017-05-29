package algorithm;

import model.UnrestrictedBoard;

public class BoardTree {
	/**
	 * Do alpha-beta pruning, returning the best move.
	 * 
	 * @param bd
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @param maximizing Black is always the maximizing player, and
	 * 		white is always the minimizing player.
	 * @param value
	 * @return
	 */
	public int alphaBeta(UnrestrictedBoard bd, int depth, int alpha, 
			int beta, boolean maximizing, int[] value) {
		if (depth == 0 || bd.someoneWins()) {
			value[0] = bd.evaluateBoard(maximizing);
			return -1;
		}
		
		return 0;
	}
}
