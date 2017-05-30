package graphics;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import model.AbstractBoard;
import model.AbstractGame;

public class BoardGraphics extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9126314175890302226L;
	private int height;
	private int width;
	private AbstractBoard bd;
	private AbstractGame game;
	private boolean activated = false;
	private Coordinate[][] grid;
	
	public BoardGraphics(int height, int width) {
		super();
		this.height = height;
		this.width = width;
		grid = new Coordinate[width][height];
	}
	
	protected void addCellsToBoard() {
		// TODO implement the case where suspension is required (for
		// single player game exclusively).
		setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Coordinate square = new Coordinate(i, j);
				square.setBackground(AbstractGame.boardColor);
				square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				square.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (!activated) {
							game.warnGameFrozen();
							return;
						}
//						if (square.isUnoccupied()) {
//							if (suspensionRequired) {
//								// First check if it is player's turn
//								if (!((SingleplayerGame) g).playerCanMove(activePlayer)) {
//									g.warnNotYourTurn();
//									return;
//								}
//								// Second, if player is allowed to take turn, update the board.
//								// TODO now the player can only be SENTE, not gote.
//								if (!((SingleplayerGame) g).updateBoardForAI
//										(square.getXCoord(), square.getYCoord(), true)) {
//									g.errorRendering();
//								}
//								Image img;
//								try {
//									if (activePlayer == Game.TURN_SENTE) {
//										img = ImageIO.read(getClass().getResource("/images/occupied.png"));
//									} else {
//										img = ImageIO.read(getClass().getResource("/images/occ.png"));
//									}
//									square.setStone(activePlayer == Game.TURN_SENTE);
//									square.setIcon(new ImageIcon(img));
//									stoneCount++;
//								} catch (IOException ee) {
//									g.errorRendering();
//								}
//								if (doEndGameCheck()) {
//									return;
//								}
//								updateActivePlayer();
//								System.out.println("Setting ai turn to true.");
//								updateIsAITurn(true);
//								return;
//							} else {
//								if (activePlayer == Game.TURN_SENTE) {
//									try {
//										Image img = ImageIO.read(getClass().getResource("/images/occupied.png"));
//										square.setIcon(new ImageIcon(img));
//									} catch (IOException e1) {
//										g.errorRendering();
//									}
//									square.setStone(true);
//									updateActivePlayer();
//								} else {
//									try {
//										Image img = ImageIO.read(getClass().getResource("/images/occ.png"));
//										square.setIcon(new ImageIcon(img));
//									} catch (IOException e1) {
//										g.errorRendering();
//									}
//									square.setStone(false);
//									updateActivePlayer();
//								}
//								lastMove2 = lastMove1;
//								lastMove1 = square;
//								stoneCount++;
//							}
//						} else {
//							g.displayOccupiedWarning();
//						}
//						doEndGameCheck();
					}
				});
				add(square);
				grid[i][j] = square;
			}
		}
	}
	
	public void reset() {
		bd.reset();
	}
	
	public void activate() {
		activated = true;
	}
	
	public void freeze() {
		activated = false;
	}
	
	public static class Coordinate extends JButton {
		private static final long serialVersionUID = -581532617710492838L;
		public int x;
		public int y;
		public Stone stone;

		public enum Stone {
			UNOCCUPIED, FIRST, SECOND
		}

		public Coordinate(int y, int x) {
			this.y = y;
			this.x = x;
			stone = Stone.UNOCCUPIED;
		}

		public boolean isUnoccupied() {
			return stone == Stone.UNOCCUPIED;
		}

		public void setStone(boolean isFirst) {
			stone = isFirst ? Stone.FIRST : Stone.SECOND;
		}

		public Stone getStone() {
			return stone;
		}

		public int getXCoord() {
			return x;
		}

		public int getYCoord() {
			return y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Coordinate other = (Coordinate) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		public void reset() {
			this.stone = Stone.UNOCCUPIED;
			this.setIcon(null);
		}
	}
}
