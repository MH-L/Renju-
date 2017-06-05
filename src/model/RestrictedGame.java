package model;

/**
 * Created by sireniazoe on 2017-05-26.
 */
public class RestrictedGame extends AbstractGame {

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
}
