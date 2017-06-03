package model;

import java.util.Random;

/**
 * Created by sireniazoe on 2017-05-26.
 */
public class UnrestrictedGame extends AbstractGame {
	private boolean playerFirst;
	private ComPlayer com;
	
	public UnrestrictedGame(boolean playerFirst, Difficulty diff) {
		super();
		this.playerFirst = playerFirst;
		bg.setupBoard(new UnrestrictedBoard());
		com = new ComPlayer((UnrestrictedBoard) bg.getBoard(), !playerFirst);
		if (playerFirst)
			isPlayerTurn = true;
	}

	@Override
	public void updateTurnStatus() {
		activePlayer = !activePlayer;
		isPlayerTurn = !isPlayerTurn;
	}
	
	private void makeFirstComMove() {
		int randInt = new Random().nextInt(3);
		int randInt2 = new Random().nextInt(3);
		int firstMove = (6 + randInt) * AbstractBoard.width + 6 + randInt2;
		bg.updateComMove(firstMove, true);
		updateTurnStatus();
	}
	
	public void comMove() {
		updateTurnStatus();
		int comMove = com.makeMove();
		System.out.println(String.format("Com Move: %s, %s", comMove / 15, comMove % 15));
		bg.updateComMove(comMove, !playerFirst);
		updateTurnStatus();
	}
	
	@Override
	public void gameStart() {
		super.gameStart();
		if (!playerFirst)
			makeFirstComMove();
	}
}
