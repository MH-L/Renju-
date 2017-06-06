package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

import javax.swing.JOptionPane;

import storage.LocalStorage;

/**
 * Created by sireniazoe on 2017-05-26.
 */
public class UnrestrictedGame extends AbstractGame {
	private boolean playerFirst;
	private ComPlayer com;
	private int turnPolicy;
	private boolean playerDropped = false;
	
	public UnrestrictedGame(int turnPolicy, Difficulty diff) {
		super();
		buttonPanel.add(btnGiveUp);
		buttonPanel.add(btnHint);
		this.turnPolicy = turnPolicy;
		updatePlayerFirst();
		
		bg.setupBoard(new UnrestrictedBoard());
		com = new ComPlayer((UnrestrictedBoard) bg.getBoard(), !playerFirst, diff);
		addGiveUpBtnListerner();
		addHintBtnListener();
	}
	
	public void addHintBtnListener() {
		btnHint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(mainFrame, "Unfortunately, computer can't "
						+ "hint you\nat this time. This is possibly due to you losing\nthe game in"
						+ " a few moves.",
						"Hint", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	
	public void addGiveUpBtnListerner() {
		btnGiveUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(mainFrame, "You lose. Better luck next time!",
						"Game Over", JOptionPane.INFORMATION_MESSAGE);
				bg.reset();
				afterGameCleanup(1);
			}
		});
	}
	
	public void updatePlayerFirst() {
		switch(turnPolicy) {
		case player_always_black:
			playerFirst = true;
			break;
		case player_always_white:
			playerFirst = false;
			break;
		case random_turn:
			playerFirst = rng.nextDouble() >= 0.5;
			break;
		case alternating_turn:
			playerFirst = true;
			break;
		default:
			playerFirst = true;
			break;
		}
	}

	@Override
	public void updateTurnStatus() {
		activePlayer = !activePlayer;
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
		btnGiveUp.setEnabled(true);
		btnHint.setEnabled(true);
		if (!playerFirst)
			makeFirstComMove();
	}
	
	@Override
	public void playerPlayed() {
		playerDropped = true;
	}

	@Override
	public void afterGameCleanup(int result) {
		// TODO Auto-generated method stub
		updatePlayerFirst();
		gameStarted.setText("Game not yet started.");
		btnGiveUp.setEnabled(false);
		btnHint.setEnabled(false);
		activePlayer = true;
		playerDropped = false;
		try {
			LocalStorage.updateGameStats(com.getDiff(), result, playerFirst);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean playerCanMove() {
		return activePlayer == playerFirst;
	}
	
	@Override
	public int exitCheck() {
		if (!playerDropped)
			return 1;
		
		int confirmValue = JOptionPane.showConfirmDialog(mainFrame,
	            "Are you sure you want to exit the current game? The current game"
	            + "\nwill be counted as a loss towards your game stats.", "Confirm exiting",
	            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (confirmValue == JOptionPane.YES_OPTION) {
			try {
				LocalStorage.updateGameStats(com.getDiff(), 1, playerFirst);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return 2;
		}
		
		return 0;
	}
}
