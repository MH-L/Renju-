package algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.UnrestrictedBoard;

public class BoardTree {
	public static int nodesNum = 0;
	/**
	 * Do alpha-beta pruning, returning the best move under given depth.
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
	public static int alphaBeta(UnrestrictedBoard bd, int depth, int alpha, 
			int beta, boolean maximizing, int[] value) {
		if (depth == 0 || bd.someoneWins()) {
			value[0] = bd.evaluateBoard();
			return -1;
		}
		
		// Sort next moves based on increment of heuristic function in descending order
		// (larger heuristic improvements will be checked earlier)
		Set<Integer> nextMoves = bd.nextMoves();
		List<Integer> nmsorted = new ArrayList<>(nextMoves);
		Map<Integer, Integer> incMap = new HashMap<>();
		for (int mv : nextMoves) {
			incMap.put(mv, bd.getInc(mv, maximizing));
		}
		nmsorted.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				int v1 = incMap.get(o1);
				int v2 = incMap.get(o2);
				if (v1 == v2)
					return 0;
				return v1 > v2 ? -1 : 1;
			}
		});
		
		int bestMove = -1;
		if (maximizing) {
			int maxVal = Integer.MIN_VALUE;
			for (int move : nmsorted) {
				bd.updateBoard(move, maximizing);
				nodesNum++;
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
			for (int move : nmsorted) {
				bd.updateBoard(move, maximizing);
				nodesNum++;
				alphaBeta(bd, depth-1, alpha, beta, !maximizing, value);
				if (value[0] < minVal) {
					minVal = value[0];
					bestMove = move;
				}
				bd.withdrawMove(move);
				beta = Math.min(beta, minVal);
				if (beta <= alpha)
					break;
			}
			
			value[0] = minVal;
		}
		
		return bestMove;
	}
}
