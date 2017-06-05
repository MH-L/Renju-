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
		switch (diff) {
		case novice:
			return BoardTree.alphaBeta(gameBoard, 3, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0});
		case intermediate:
			return BoardTree.alphaBeta(gameBoard, 5, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0});
		case advanced:
			int threatSearch = BoardTree.threatSpaceSearch(gameBoard, 20, isComFirst);
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				return threatSearch;
			}
			
			return BoardTree.alphaBeta(gameBoard, 7, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0});
		case ultimate:
			threatSearch = BoardTree.threatSpaceSearch(gameBoard, 30, isComFirst);
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				return threatSearch;
			}
			
			return BoardTree.alphaBeta(gameBoard, 8, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0});
		default:
			threatSearch = BoardTree.threatSpaceSearch(gameBoard, 25, isComFirst);
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				return threatSearch;
			}
			
			return BoardTree.alphaBeta(gameBoard, 7, Integer.MIN_VALUE, 
					Integer.MAX_VALUE, isComFirst, new int[]{0});
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
