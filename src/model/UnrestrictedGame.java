package model;

/**
 * Created by sireniazoe on 2017-05-26.
 */
public class UnrestrictedGame extends AbstractGame {
	private boolean playerFirst;
	
	public UnrestrictedGame(boolean playerFirst, Difficulty diff) {
		super();
		this.playerFirst = playerFirst;
		bg.setupBoard(new UnrestrictedBoard());
	}

	@Override
	public void updateTurnStatus() {
		activePlayer = !activePlayer;
		isPlayerTurn = !isPlayerTurn;
	}
}
