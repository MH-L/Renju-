package model;

import algorithm.BoardTree;

public class ComPlayer extends AbstractPlayer {
	private UnrestrictedBoard gameBoard;
	private boolean isComFirst;
	private int moveNum = 0;
	
	ComPlayer(UnrestrictedBoard bd, boolean isComFirst) {
		gameBoard = bd;
		this.isComFirst = isComFirst;
		moveNum = 0;
	}
	
	@Override
	public int makeMove() {
		if (moveNum > 5) {
			int threatSearch = BoardTree.threatSpaceSearch(gameBoard, 20, isComFirst);
			if (threatSearch >= 0)
				return threatSearch;
		}
		
		return BoardTree.alphaBeta(gameBoard, 6, Integer.MIN_VALUE, 
				Integer.MAX_VALUE, isComFirst, new int[]{0});
	}

	@Override
	public boolean withdraw() {
		// TODO Auto-generated method stub
		return false;
	}

}
