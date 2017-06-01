package model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import graphics.BoardGraphics;

public abstract class AbstractGame {
	protected static final Font smallGameFont = new Font("Open Sans",
			Font.PLAIN, 28);
	protected static final Font largeGameFont = new Font("Open Sans",
			Font.PLAIN, 40);
	protected static final Font mediumGameFont = new Font("Open Sans",
			Font.PLAIN, 35);
	protected static final Font tinyGameFont = new Font("Open Sans",
			Font.PLAIN, 16);
	public static final Insets emptyMargin = new Insets(0, 0, 0, 0);
	public static final Color boardColor = new Color(204, 204, 0);
	public static final Dimension defaultFrameDimension = new Dimension(1400,
			760);
	public static final Dimension defaultFrameSmall = new Dimension(700, 500);
	public static final int functionPanelWidth = 295;
	protected JPanel mainPanel;
	protected JPanel parentPanel;
	protected JPanel chatPanel;
	protected JFrame mainFrame;
	protected JButton btnStart;
	protected JButton btnGiveUp;
	protected JPanel titlePanel;
	protected JMenuBar menuBar;
	protected JPanel historyPanel;
	protected JPanel buttonPanel;
	protected JPanel functionPanel;
	private JLabel gameStarted;
	protected JTextArea messageArea;
	protected BoardGraphics bg;
	
	protected AbstractPlayer player1;
	protected AbstractPlayer player2;
	protected boolean isPlayerTurn = false;
	protected boolean activePlayer = true;
	
	public enum Difficulty {
		novice, intermediate, advanced, ultimate;
	}
	
	public enum Result {
		UNDECIDED, SENTE, GOTE, TIE
	}
	
	public AbstractGame() {
		parentPanel = new JPanel(new BorderLayout());
		chatPanel = new JPanel(new BorderLayout());
		chatPanel.setPreferredSize(new Dimension(395, 700));
		mainPanel = new JPanel(new BorderLayout());
		mainFrame = new JFrame("Gomoku Plus");
		mainFrame.setSize(defaultFrameDimension);
		btnStart = Main.getPlainLookbtn("Start!", "Open Sans", 23, Font.PLAIN, Color.CYAN);
		btnGiveUp = Main.getPlainLookbtn("Give UP!", "Open Sans", 23, Font.PLAIN, Color.RED);
		btnStart.setMargin(emptyMargin);
		btnGiveUp.setMargin(emptyMargin);
		parentPanel.add(mainPanel, BorderLayout.WEST);
		parentPanel.add(new JSeparator());
		parentPanel.add(chatPanel, BorderLayout.EAST);
		mainFrame.add(parentPanel);
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addCloseConfirmation(mainFrame);

		functionPanel = new JPanel(new BorderLayout());
		functionPanel.setPreferredSize(new Dimension(functionPanelWidth, 700));
		buttonPanel = new JPanel(new GridLayout(2, 2));
		buttonPanel.setPreferredSize(new Dimension(functionPanelWidth, 200));
		titlePanel = new JPanel();
		titlePanel.setPreferredSize(new Dimension(functionPanelWidth, 100));
		historyPanel = new JPanel(new GridLayout(4, 1));
		functionPanel.add(titlePanel, BorderLayout.NORTH);
		functionPanel.add(historyPanel, BorderLayout.CENTER);
		functionPanel.add(buttonPanel, BorderLayout.SOUTH);

		gameStarted = new JLabel("Game not yet started.");
		gameStarted.setFont(smallGameFont);
		historyPanel.add(gameStarted);

		bg = new BoardGraphics(AbstractBoard.height, AbstractBoard.width, this);
		bg.setPreferredSize(new Dimension(700, 700));

		menuBar = createJMenuBar();
		mainFrame.setJMenuBar(menuBar);
		buttonPanel.add(btnStart);
		buttonPanel.add(btnGiveUp);
		mainPanel.add(bg, BorderLayout.LINE_START);
		mainPanel.add(new JSeparator(SwingConstants.VERTICAL));
		mainPanel.add(functionPanel, BorderLayout.LINE_END);

		messageArea = new JTextArea(4, 40);
		messageArea.setFont(smallGameFont);
		chatPanel.add(messageArea, BorderLayout.CENTER);
		
		addStartButtonListener(btnStart);
	}
	
	protected void addStartButtonListener(JButton btn) {
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (gameStarted.getText().equals("Game Started.")) {
					JOptionPane.showMessageDialog(mainFrame, "The game already started.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				gameStart();
			}
		});
	}
	
	private JMenuBar createJMenuBar() {
		JMenuBar menus = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem newGame = new JMenuItem("New Game (F12)");
		newGame.setFont(smallGameFont);
		newGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gameStart();
			}
		});

		gameMenu.add(newGame);
		gameMenu.setPreferredSize(new Dimension(166, 60));
		gameMenu.setFont(smallGameFont);
		gameMenu.addSeparator();
		JMenuItem loadGame = new JMenuItem("Load Game (F11)");
		loadGame.setFont(smallGameFont);
		gameMenu.add(loadGame);
		gameMenu.addSeparator();
		JMenuItem exit = new JMenuItem("Exit to main menu");
		exit.setFont(smallGameFont);
		gameMenu.add(exit);
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispose();
				Main.displayWelcomeFrame();
			}
		});

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setPreferredSize(new Dimension(166, 60));
		helpMenu.setFont(smallGameFont);
		JMenuItem about = new JMenuItem("About");
		JMenuItem onlineHelp = new JMenuItem("Online Help");
		about.setFont(smallGameFont);
		onlineHelp.setFont(smallGameFont);
		helpMenu.add(about);
		helpMenu.addSeparator();
		helpMenu.add(onlineHelp);

		JMenu optionsMenu = new JMenu("Options");
		optionsMenu.setPreferredSize(new Dimension(168, 60));
		optionsMenu.setFont(smallGameFont);
		JMenuItem soundOption = new JMenuItem("Sound");
		JMenuItem animationOption = new JMenuItem("Animation");
		soundOption.setFont(smallGameFont);
		animationOption.setFont(smallGameFont);
		optionsMenu.add(animationOption);
		optionsMenu.addSeparator();
		optionsMenu.add(soundOption);

		menus.add(gameMenu);
		menus.add(new JSeparator(SwingConstants.VERTICAL));
		menus.add(helpMenu);
		menus.add(new JSeparator(SwingConstants.VERTICAL));
		menus.add(optionsMenu);
		menus.setPreferredSize(new Dimension(500, 60));
		return menus;
	}
	
	public void displayOccupiedWarning() {
		JOptionPane.showMessageDialog(mainFrame, "The square is already occupied.",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	public void errorRendering() {
		JOptionPane.showMessageDialog(mainFrame, "Unable to render board image. Fatal error!",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	public void displayWinnerInfo(boolean isSente) {
		String winnerInfo = isSente ? "Black" : "White";
		JOptionPane.showMessageDialog(null, winnerInfo + " wins!",
				"Game Over", JOptionPane.INFORMATION_MESSAGE);
	}

	public void warnGameFrozen() {
		JOptionPane.showMessageDialog(mainFrame, "Game is not yet started or has finished.\nPlease start new game by pressing"
				+ " start\nor go to menu bar.", "Game Status Info", JOptionPane.INFORMATION_MESSAGE);
	}

	public void displayTieMessageBoardFull() {
		JOptionPane.showMessageDialog(mainFrame, "Board Full. Game comes to a tie.",
				"Game Over", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void warnNotYourTurn() {
		JOptionPane.showMessageDialog(mainFrame, "Not Your Turn!",
				"Computer Making Move", JOptionPane.WARNING_MESSAGE);
	}

	public static void addCloseConfirmation(JFrame frame) {
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame,
		            "Are you sure to close this window?", "Confirm Closing",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
		            System.exit(0);
		        }
		    }
		});
	}
	
	protected void gameStart() {
		bg.reset();
		bg.activate();
		gameStarted.setText("Game Started.");
	}
	
	public boolean playerCanMove() {
		return isPlayerTurn;
	}
	
	public abstract void updateTurnStatus();
	
	public abstract void comMove();
	
	public boolean isBlackActive() {
		return activePlayer;
	}
}
