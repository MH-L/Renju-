package algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
		return alphaBetaMem(bd, depth, alpha, beta, maximizing, value, -1);
	}
	
	public static int alphaBetaMem(UnrestrictedBoard bd, int depth, int alpha, 
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
		
		int winningMove = bd.canWinNextMove(maximizing);
		if (winningMove >= 0) {
			value[0] = maximizing ? AbstractBoard.winning_score : -AbstractBoard.winning_score;
			return winningMove;
		}
		
		// TODO prune next move if the opponent has threats of fours and threes
		// Note that the former is easy, as implemented below; but the latter is
		// hard since we may use global refutation so that threats are to be 
		// dealt with in later moves.
		int[] blocking = new int[]{0};
		if (lastMove >= 0 && bd.formedThreat(!maximizing, lastMove, blocking)) {
			int onlyMove = blocking[0];
			bd.updateBoard(onlyMove, maximizing);
			alphaBetaMem(bd, depth - 1, alpha, beta, !maximizing, value, onlyMove);
			bd.withdrawMove(onlyMove);
			return onlyMove;
		}
		
		Set<Integer> nextMoves = new HashSet<>();
		Set<Integer> allThrees = bd.findAllThrees(!maximizing);
		if (!allThrees.isEmpty()) {
			Map<Integer, Integer> thLocations = bd.findThreatLocation(maximizing);
			nextMoves = allThrees;
			nextMoves.addAll(thLocations.keySet());
		} else {
			nextMoves = bd.nextMoves();
		}
		
		// Sort next moves based on increment of heuristic function in descending order
		// (larger heuristic improvements will be checked earlier)
		
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
				alphaBetaMem(bd, depth-1, alpha, beta, !maximizing, value, move);
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
				alphaBetaMem(bd, depth-1, alpha, beta, !maximizing, value, move);
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
	
	public static int alphaBetaCustom(UnrestrictedBoard bd, int depth, int alpha, 
			int beta, boolean maximizing, int[] value, int lastMove, 
			int evalOscillation, int selectionThreshold) {

		if (depth == 0) {
			nodesNum++;
			value[0] = bd.evaluateBoardRng(evalOscillation);
			return -1;
		}
		
		if ((lastMove >= 0 && bd.checkWinningLite(lastMove, !maximizing)) || 
				bd.someoneWins()) {
			nodesNum++;
			value[0] = bd.evaluateBoardRng(evalOscillation);
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
			if (inc >= selectionThreshold) {
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
				alphaBetaCustom(bd, depth-1, alpha, beta, !maximizing, value, move, 4, 4);
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
				alphaBetaCustom(bd, depth-1, alpha, beta, !maximizing, value, move, 4, 4);
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
	 * (This version of threat space searcher does not guarantee to find the shortest sequence).
	 * @param bd
	 * @param depth
	 * @param first
	 * @return
	 */
	public static int threatSpaceSearch(UnrestrictedBoard bd, int depth, boolean first, int[] blocking) {
		if (depth <= 0)
			return -100;
		
		int selfWinningLoc = bd.canWinNextMove(first);
		if (selfWinningLoc >= 0)
			return selfWinningLoc;
		int blockingLoc = bd.canWinNextMove(!first);
		if (blockingLoc >= 0) {
			blocking[0] = blockingLoc;
			return -200;
		}
		
		Map<Integer, Integer> threatAndCounter = bd.findThreatLocation(first);
		
		for (Entry<Integer, Integer> pair : threatAndCounter.entrySet()) {
			bd.updateBoard(pair.getKey(), first);
			bd.updateBoard(pair.getValue(), !first);
			int childResult = threatSpaceSearch(bd, depth - 1, first, blocking);
			bd.withdrawMove(pair.getKey());
			bd.withdrawMove(pair.getValue());
			if (childResult >= 0)
				return pair.getKey();
		}
		
		return -100;
	}
	
	/**
	 * Shortest-path threat space searcher: generates the winning threat sequence
	 * (if there are any) with the smallest number of rounds.
	 * @param bd
	 * @param depth
	 * @param first
	 * @param blocking
	 * @param pathLen stores the length of the shortest threat sequence
	 * @return
	 */
	public static int threatSpaceSearchV2(UnrestrictedBoard bd, int depth, boolean first, 
			int[] blocking, int[] pathLen) {
		if (depth <= 0)
			return -100;
		
		int selfWinningLoc = bd.canWinNextMove(first);
		if (selfWinningLoc >= 0) {
			pathLen[0] = 1;
			return selfWinningLoc;
		}
		
		int blockingLoc = bd.canWinNextMove(!first);
		if (blockingLoc >= 0) {
			blocking[0] = blockingLoc;
			return -200;
		}
		
		Map<Integer, Integer> threatAndCounter = bd.findThreatLocation(first);
		
		int bestChild = -1, bestLen = Integer.MAX_VALUE;
		for (Entry<Integer, Integer> pair : threatAndCounter.entrySet()) {
			int threatingMove = pair.getKey(), forcedMove = pair.getValue();
			bd.updateBoard(threatingMove, first);
			bd.updateBoard(forcedMove, !first);
			int childResult = threatSpaceSearchV2(bd, depth - 1, first, blocking, pathLen);
			bd.withdrawMove(threatingMove);
			bd.withdrawMove(forcedMove);
			if (childResult >= 0 && pathLen[0] < bestLen) {
				bestChild = threatingMove;
				bestLen = pathLen[0];
			}
		}
		
		if (bestChild >= 0) {
			pathLen[0] = bestLen + 2;
			return bestChild;
		}
		
		return -100;
	}
	
	public static List<Integer> genSortedNextMoves(UnrestrictedBoard bd, boolean first) {
		return null;
	}
}
