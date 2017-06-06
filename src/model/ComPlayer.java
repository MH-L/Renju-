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
			return BoardTree.alphaBetaMem(gameBoard, 3, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove);
		case intermediate:
			return BoardTree.alphaBetaMem(gameBoard, 5, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove);
		case advanced:
			int threatSearch = BoardTree.threatSpaceSearch(gameBoard, 20, isComFirst);
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				return threatSearch;
			}
			
			return BoardTree.alphaBetaMem(gameBoard, 7, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove);
		case ultimate:
			threatSearch = BoardTree.threatSpaceSearch(gameBoard, 30, isComFirst);
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				return threatSearch;
			}
			
			return BoardTree.alphaBetaMem(gameBoard, 8, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove);
		default:
			threatSearch = BoardTree.threatSpaceSearch(gameBoard, 25, isComFirst);
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				return threatSearch;
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
