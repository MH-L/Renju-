package model;

import algorithm.BoardTree;
import model.AbstractGame.Difficulty;

public class ComPlayer extends AbstractPlayer {
	private UnrestrictedBoard gameBoard;
	private boolean isComFirst;
	private Difficulty diff;
	
	ComPlayer(UnrestrictedBoard bd, boolean isComFirst, Difficulty diff) {
		gameBoard = bd;
		this.isComFirst = isComFirst;
		this.diff = diff;
	}
	
	@Override
	public int makeMove() {
		int lastMove = gameBoard.getMostRecentMove();
		switch (diff) {
		case novice:
			int[] blocking = new int[]{0};
			// Threat searcher set to depth = 1 just to detect direct threat
			int threatSearch = BoardTree.threatSpaceSearch(gameBoard, 1, isComFirst, blocking);
			if (threatSearch == -200)
				return blocking[0];
			return BoardTree.alphaBetaMem(gameBoard, 3, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove);
		case intermediate:
			blocking = new int[]{0};
			threatSearch = BoardTree.threatSpaceSearch(gameBoard, 1, isComFirst, blocking);
			if (threatSearch == -200)
				return blocking[0];
			return BoardTree.alphaBetaMem(gameBoard, 5, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove);
		case advanced:
			blocking = new int[]{0};
			threatSearch = BoardTree.threatSpaceSearch(gameBoard, 20, isComFirst, blocking);
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				return threatSearch;
			} else if (threatSearch == -200) {
				// Direct threat detected by threat space searcher.
				// If this falls down to alpha beta searcher then it's disaster.
				return blocking[0];
			}
			
			return BoardTree.alphaBetaMem(gameBoard, 7, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove);
		case ultimate:
			blocking = new int[]{0};
			threatSearch = BoardTree.threatSpaceSearchV2(gameBoard, 30, isComFirst, blocking, new int[]{0});
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				return threatSearch;
			} else if (threatSearch >= -200) {
				// Direct threat detected by threat space searcher.
				return blocking[0];
			}
			
			return BoardTree.alphaBetaMem(gameBoard, 8, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove);
		default:
			blocking = new int[]{0};
			threatSearch = BoardTree.threatSpaceSearchV2(gameBoard, 25, isComFirst, blocking, new int[]{0});
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				return threatSearch;
			} else if (threatSearch == -200) {
				return blocking[0];
			}
			
			return BoardTree.alphaBetaMem(gameBoard, 7, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove);
		}
	}
	
	public Difficulty getDiff() {
		return diff;
	}

	@Override
	public boolean withdraw() {
		// TODO Auto-generated method stub
		return false;
	}

}
