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
import model.UnrestrictedBoard;

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
						if (square.isUnoccupied()) {
							if (game.isBlackActive()) {
								try {
									Image img = ImageIO.read(getClass().getResource("/images/occupied.png"));
									square.setIcon(new ImageIcon(img));
								} catch (IOException e1) {
									game.errorRendering();
								}
								square.setStone(true);
								game.updateTurnStatus();
							} else {
								try {
									Image img = ImageIO.read(getClass().getResource("/images/occ.png"));
									square.setIcon(new ImageIcon(img));
								} catch (IOException e1) {
									game.errorRendering();
								}
								square.setStone(false);
								game.updateTurnStatus();
							}
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

	public void setupBoard(AbstractBoard bd) {
		this.bd = bd;
	}
}
