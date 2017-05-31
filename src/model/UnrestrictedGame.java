package model;

/**
 * Created by sireniazoe on 2017-05-26.
 */
public class UnrestrictedGame extends AbstractGame {
	private boolean playerFirst;
	
	public UnrestrictedGame(boolean playerFirst, Difficulty diff) {
		super();
		this.playerFirst = playerFirst;
		
	}

	@Override
	public void updateTurnStatus() {
		isPlayerTurn = !isPlayerTurn;
	}
}
