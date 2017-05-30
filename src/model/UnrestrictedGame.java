package model;

/**
 * Created by sireniazoe on 2017-05-26.
 */
public class UnrestrictedGame extends AbstractGame {
	public UnrestrictedGame(boolean playerFirst, Difficulty diff) {
		if (playerFirst)
			playerFirst = true;
		
	}

	@Override
	public void updateTurnStatus() {
		isPlayerTurn = !isPlayerTurn;
	}
}
