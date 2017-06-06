package model;

public class UnrestrictedCvCGame extends AbstractGame {
	private ComPlayer com1;
	private ComPlayer com2;
	
	public UnrestrictedCvCGame(Difficulty blackDiff, Difficulty whiteDiff) {
		bg.setupBoard(new UnrestrictedBoard());
		com1 = new ComPlayer((UnrestrictedBoard) bg.getBoard(), true, blackDiff);
		com2 = new ComPlayer((UnrestrictedBoard) bg.getBoard(), false, whiteDiff);
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
	
}
