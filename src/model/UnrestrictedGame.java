package model;

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
	
	public void comMove() {
		updateTurnStatus();
		int comMove = com.makeMove();
		System.out.println(String.format("Com Move: %s, %s", comMove / 15, comMove % 15));
		bg.updateComMove(comMove, !playerFirst);
		updateTurnStatus();
	}
}
