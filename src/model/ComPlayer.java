package model;

import algorithm.BoardTree;

public class ComPlayer extends AbstractPlayer {
	private UnrestrictedBoard gameBoard;
	private boolean isComFirst;
	
	ComPlayer(UnrestrictedBoard bd, boolean isComFirst) {
		gameBoard = bd;
		this.isComFirst = isComFirst;
	}
	
	@Override
	public int makeMove() {
		return BoardTree.alphaBeta(gameBoard, 6, Integer.MIN_VALUE, 
				Integer.MAX_VALUE, isComFirst, new int[]{0});
	}

	@Override
	public boolean withdraw() {
		// TODO Auto-generated method stub
		return false;
	}

}
