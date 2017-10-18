package model;

import algorithm.BoardTree;
import model.AbstractGame.Difficulty;
import storage.LocalStorage;

public class ComPlayer extends AbstractPlayer {
	private UnrestrictedBoard gameBoard;
	private boolean isComFirst;
	private Difficulty diff;
	private int oscillation = 0;
	private int selectionThreshold = -1;

	ComPlayer(UnrestrictedBoard bd, boolean isComFirst, Difficulty diff) {
		gameBoard = bd;
		this.isComFirst = isComFirst;
		this.diff = diff;
	}

	public void setCustomParams(int oscillation, int selectionThreshold) {
	    this.oscillation = oscillation;
	    this.selectionThreshold = selectionThreshold;
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
			return BoardTree.alphaBeta(gameBoard, 7, Integer.MIN_VALUE, Integer.MAX_VALUE, isComFirst);
		case intermediate:
			blocking = new int[]{0};
			threatSearch = BoardTree.threatSpaceSearch(gameBoard, 15, isComFirst, blocking);
            if (threatSearch >= 0) {
                System.out.println("Threat space searcher found sequence: " + threatSearch);
                BoardTree.cachedLocs.add(gameBoard.getZobristHash());
                LocalStorage.addCritical(gameBoard.getZobristHash());
                return threatSearch;
            } else if (threatSearch == -200)
				return blocking[0];
			return BoardTree.alphaBeta(gameBoard, 8, Integer.MIN_VALUE, Integer.MAX_VALUE, isComFirst);
		case advanced:
			blocking = new int[]{0};
			threatSearch = BoardTree.threatSpaceSearch(gameBoard, 20, isComFirst, blocking);
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				boolean result = BoardTree.cachedLocs.add(gameBoard.getZobristHash());
                if (result) LocalStorage.addCritical(gameBoard.getZobristHash());
				return threatSearch;
			} else if (threatSearch == -200) {
				// Direct threat detected by threat space searcher.
				// If this falls down to alpha beta searcher then it's disaster.
				return blocking[0];
			}

			return BoardTree.alphaBeta(gameBoard, 9, Integer.MIN_VALUE, Integer.MAX_VALUE, isComFirst);
		case ultimate:
			blocking = new int[]{0};
			threatSearch = BoardTree.threatSpaceSearchV2(gameBoard, 40, isComFirst, blocking, new int[]{0});
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
                boolean result = BoardTree.cachedLocs.add(gameBoard.getZobristHash());
                if (result) LocalStorage.addCritical(gameBoard.getZobristHash());
				return threatSearch;
			} else if (threatSearch == -200) {
				// Direct threat detected by threat space searcher.
				return blocking[0];
			}

			return BoardTree.alphaBeta(gameBoard, 11, Integer.MIN_VALUE, Integer.MAX_VALUE, isComFirst);
        case custom:
            blocking = new int[]{0};
            threatSearch = BoardTree.threatSpaceSearchV2(gameBoard, 40, isComFirst, blocking, new int[]{0});
            if (threatSearch >= 0) {
                System.out.println("Threat space searcher found sequence: " + threatSearch);
                boolean result = BoardTree.cachedLocs.add(gameBoard.getZobristHash());
                if (result) LocalStorage.addCritical(gameBoard.getZobristHash());
                return threatSearch;
            } else if (threatSearch == -200) {
                // Direct threat detected by threat space searcher.
                return blocking[0];
            }

            return BoardTree.alphaBetaCustom(gameBoard, 12, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove, oscillation, selectionThreshold, 0, true);
		default:
			blocking = new int[]{0};
			threatSearch = BoardTree.threatSpaceSearchV2(gameBoard, 25, isComFirst, blocking, new int[]{0});
			if (threatSearch >= 0) {
				System.out.println("Threat space searcher found sequence: " + threatSearch);
				return threatSearch;
			} else if (threatSearch == -200) {
				return blocking[0];
			}

			return BoardTree.alphaBetaCustom(gameBoard, 8, Integer.MIN_VALUE,
					Integer.MAX_VALUE, isComFirst, new int[]{0}, lastMove, oscillation, selectionThreshold, 0, false);
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
