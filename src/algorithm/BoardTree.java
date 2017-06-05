package algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import model.AbstractBoard;
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
		return alphaBetaHelper(bd, depth, alpha, beta, maximizing, value, -1);
	}
	
	private static int alphaBetaHelper(UnrestrictedBoard bd, int depth, int alpha, 
			int beta, boolean maximizing, int[] value, int lastMove) {
		if (depth == 0) {
			nodesNum++;
			value[0] = bd.evaluateBoard();
			return -1;
		}
		
		if ((lastMove >= 0 && bd.checkWinningLite(lastMove, !maximizing)) || 
				bd.someoneWins()) {
			nodesNum++;
			value[0] = bd.evaluateBoard();
			return -1;
		}
		
		// Sort next moves based on increment of heuristic function in descending order
		// (larger heuristic improvements will be checked earlier)
		Set<Integer> nextMoves = bd.nextMoves();
		if (nextMoves.isEmpty()) {
			if (bd.boardFull()) {
				value[0] = bd.evaluateBoard();
				return -1;
			} else {
				return bd.getFirstRandomMove();
			}
		}
		
		List<Integer> nmsorted = new ArrayList<>();
		Map<Integer, Integer> incMap = new HashMap<>();
		for (int mv : nextMoves) {
			int inc = bd.getInc(mv, maximizing);
			// TODO best-looking moves are checked (Allis, 1994)
			// TODO inc function might be buggy
			if (inc > 3) {
				nmsorted.add(mv);
				incMap.put(mv, inc);
			}
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
		
		if (nmsorted.isEmpty())
			nmsorted.addAll(nextMoves);
		
		Integer maxInc = incMap.get(nmsorted.get(0));
		if (maxInc != null && maxInc >= AbstractBoard.winning_score / 2) {
			value[0] = maximizing ? AbstractBoard.winning_score : -AbstractBoard.winning_score;
			return nmsorted.get(0);
		}
		
		int bestMove = -1;
		if (maximizing) {
			int maxVal = Integer.MIN_VALUE;
			for (int move : nmsorted) {
				bd.updateBoard(move, maximizing);
				nodesNum++;
				alphaBetaHelper(bd, depth-1, alpha, beta, !maximizing, value, move);
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
				alphaBetaHelper(bd, depth-1, alpha, beta, !maximizing, value, move);
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
	
	/**
	 * Threat-space search version 1; initial version only considers direct threats (4 stones).
	 * @param bd
	 * @param depth
	 * @param first
	 * @return
	 */
	public static int threatSpaceSearch(UnrestrictedBoard bd, int depth, boolean first) {
		if (depth <= 0)
			return -100;
		if (bd.canWinNextMove(!first) >= 0)
			return -100;
		
		int selfWinningLoc = bd.canWinNextMove(first);
		if (selfWinningLoc >= 0)
			return selfWinningLoc;
		
		Map<Integer, Integer> threatAndCounter = bd.findThreatLocation(first);
//		System.out.println(threatAndCounter.size() + " threat locations found!");
		
		for (Entry<Integer, Integer> pair : threatAndCounter.entrySet()) {
			bd.updateBoard(pair.getKey(), first);
			bd.updateBoard(pair.getValue(), !first);
			int childResult = threatSpaceSearch(bd, depth - 1, first);
			bd.withdrawMove(pair.getKey());
			bd.withdrawMove(pair.getValue());
			if (childResult >= 0)
				return pair.getKey();
		}
		
		return -100;
	}
}
