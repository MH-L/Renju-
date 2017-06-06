package graphics;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
	
	public enum Stone {
		UNOCCUPIED, FIRST, SECOND
	}
	
	public BoardGraphics(int height, int width, AbstractGame game) {
		super(new GridLayout(height, width));
		this.height = height;
		this.width = width;
		grid = new Coordinate[width][height];
		addCellsToBoard();
		this.game = game;
	}
	
	protected void addCellsToBoard() {
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
						
						if (!game.playerCanMove()) {
							// TODO maybe display warnings??
							return;
						}
						
						if (square.isUnoccupied()) {
							game.playerPlayed();
							if (game.isBlackActive()) {
								square.setStone(true);
								bd.updateBoard(square.y * width + square.x, true);
							} else {
								square.setStone(false);
								bd.updateBoard(square.y * width + square.x, false);
							}
							if (bd.someoneWins()) {
								game.displayWinnerInfo(true);
								game.afterGameCleanup(0);
								reset();
								return;
							} else if (bd.boardFull()) {
								game.displayTieMessageBoardFull();
								game.afterGameCleanup(2);
								reset();
								return;
							}
							new Thread(new Runnable() {
								@Override
								public void run() {
									game.comMove();
								}
							}).start();
						} else {
							game.displayOccupiedWarning();
						}
					}
				});
				add(square);
				grid[i][j] = square;
			}
		}
	}
	
	public int updateComMove(int move, boolean first) {
		bd.updateBoard(move, first);
		int rowIdx = move / width;
		int colIdx = move % width;
		grid[rowIdx][colIdx].setStone(first);
		if (bd.someoneWins()) {
			game.displayWinnerInfo(false);
			game.afterGameCleanup(1);
			reset();
			return 1;
		} else if (bd.boardFull()) {
			game.displayTieMessageBoardFull();
			game.afterGameCleanup(2);
			reset();
			return 2;
		}
		
		return 0;
	}
	
	public void reset() {
		activated = false;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				grid[i][j].reset();
			}
		}
		bd.reset();
	}
	
	public void activate() {
		activated = true;
	}
	
	public void freeze() {
		activated = false;
	}
	
	public class Coordinate extends JButton {
		private static final long serialVersionUID = -581532617710492838L;
		public int x;
		public int y;
		public Stone stone;

		public Coordinate(int y, int x) {
			this.y = y;
			this.x = x;
			stone = Stone.UNOCCUPIED;
		}

		public boolean isUnoccupied() {
			return stone == Stone.UNOCCUPIED;
		}

		public void setStone(boolean isFirst) {
			String resourcesStr = "";
			if (isFirst)
				resourcesStr = "/images/occupied.png";
			else
				resourcesStr = "/images/occ.png";	
			try {
				Image img = ImageIO.read(getClass().getResource(resourcesStr));
				setIcon(new ImageIcon(img));
			} catch (IOException e1) {
				game.errorRendering();
			}
			stone = isFirst ? Stone.FIRST : Stone.SECOND;
		}
		
		public void resetSq() {
			setIcon(null);
			stone = Stone.UNOCCUPIED;
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

	public void setupBoard(AbstractBoard bd) {
		this.bd = bd;
	}
	
	public AbstractBoard getBoard() {
		return bd;
	}
}
