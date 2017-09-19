package model;

public class UnrestrictedCvCGame extends AbstractGame {
	private ComPlayer com1;
	private ComPlayer com2;
	private UnrestrictedBoard board;
	
	public UnrestrictedCvCGame(Difficulty blackDiff, Difficulty whiteDiff) {
		bg.setupBoard(new UnrestrictedBoard());
		com1 = new ComPlayer((UnrestrictedBoard) bg.getBoard(), true, blackDiff);
		com2 = new ComPlayer((UnrestrictedBoard) bg.getBoard(), false, whiteDiff);
	}

	public UnrestrictedCvCGame(UnrestrictedBoard ub, Difficulty blackDiff, Difficulty whiteDiff) {
	    board = ub;
        com1 = new ComPlayer(board, true, blackDiff);
        com2 = new ComPlayer(board, false, whiteDiff);
    }

	public UnrestrictedCvCGame(ComPlayer p1, ComPlayer p2) {
		com1 = p1;
		com2 = p2;
	}

	public void setCustomAIParams(int oscillation, int selectionThreshold) {
		com1.setCustomParams(oscillation, selectionThreshold);
		com2.setCustomParams(oscillation, selectionThreshold);
	}

	@Override
	public void updateTurnStatus() {
		activePlayer = !activePlayer;
	}

	@Override
	public void comMove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterGameCleanup(int result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean playerCanMove() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void gameStart() {
		super.gameStart();
		new Thread(new Runnable() {
			@Override
			public void run() {
				runCvCGame();
			}
		}).start();
	}
	
	public void runCvCGame() {
	    activePlayer = true;
		while (true) {
			int moveResult = 0;
			if (activePlayer) {
				int comMove = com1.makeMove();
				moveResult = bg.updateComMove(comMove, true);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				int comMove = com2.makeMove();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				moveResult = bg.updateComMove(comMove, false);
			}
			
			if (moveResult != 0)
				break;
			
			updateTurnStatus();
		}
	}

	public void runCvCGameForRecord(int times) {
		for (int i = 0; i < times; i++) {
			int result = runCvCGameNoGraphics();
			board.writeRecords(result);
            board.reset();
		}
	}

	private int runCvCGameNoGraphics() {
		while (true) {
			int moveResult = 0;
			if (activePlayer) {
				int comMove = com1.makeMove();
				board.updateBoard(comMove, true);
                System.out.println("Computer 1 Move: " + comMove);
                board.addMoveToSequence(comMove);
                if (board.someoneWins()) {
                    moveResult = 1;
                } else if (board.boardFull()) {
                    moveResult = 3;
                }
            } else {
			    // TODO buggy
				int comMove = com2.makeMove();
				board.updateBoard(comMove, false);
                System.out.println("Computer 2 Move: " + comMove);
                board.addMoveToSequence(comMove);
                if (board.someoneWins()) {
                    moveResult = 2;
                } else if (board.boardFull()) {
                    moveResult = 3;
                }
            }

			if (moveResult != 0) {
                System.out.println("Game Finished, Result: " + moveResult);
                return moveResult;
			}

			updateTurnStatus();
		}
	}
	
}
