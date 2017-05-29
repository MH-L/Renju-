package algorithm;

import java.util.Set;

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
		
		int bestMove = -1;
		if (maximizing) {
			int maxVal = Integer.MIN_VALUE;
			Set<Integer> nextMoves = bd.nextMoves();
			for (int move : nextMoves) {
				bd.updateBoard(move, maximizing);
				alphaBeta(bd, depth-1, alpha, beta, !maximizing, value);
				if (value[0] > maxVal) {
					maxVal = value[0];
					bestMove = move;
				}
				
				bd.withdrawMove(move);
				alpha = Math.max(alpha, maxVal);
				if (beta <= alpha)
					break;
			}
			
			value[0] = maxVal;
		} else {
			int minVal = Integer.MAX_VALUE;
			Set<Integer> nextMoves = bd.nextMoves();
			for (int move : nextMoves) {
				bd.updateBoard(move, maximizing);
				alphaBeta(bd, depth-1, alpha, beta, !maximizing, value);
				if (value[0] < minVal) {
					minVal = value[0];
					bestMove = move;
				}
				bd.withdrawMove(move);
				alpha = Math.min(alpha, minVal);
				if (beta <= alpha)
					break;
			}
		}
		
		return bestMove;
	}
}
