package model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import algorithm.BoardTree;
import algorithm.Zobrist;
import model.AbstractGame.Difficulty;
import storage.LocalStorage;

public class Main {
	private static AbstractGame game;
	private static final Font panelSubTitleFont = new Font("Tahoma", Font.PLAIN, 35);
	private static final Font radioBtnsFont = new Font("Calibri", Font.PLAIN, 32);
	
	public static void main(String args[]) throws IOException {
		LocalStorage.initializeLocalStorage();
		LocalStorage.writeInitialSeed();
		UIManager.put("OptionPane.messageFont", AbstractGame.smallGameFont);
		UIManager.put("OptionPane.buttonFont", AbstractGame.smallGameFont);
		setUIFont(new FontUIResource(new Font("Calibri", Font.PLAIN, 16)));
		displayWelcomeFrame();
	}
	
	public static void setUIFont (javax.swing.plaf.FontUIResource f){
	    java.util.Enumeration keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	        Object key = keys.nextElement();
	        Object value = UIManager.get (key);
	        if (value != null && value instanceof javax.swing.plaf.FontUIResource)
	    	    UIManager.put (key, f);
	    }
	} 

	private static class RoundedBorder implements Border {
		private int radius;

		private RoundedBorder(int radius) {
			this.radius = radius;
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
		}
	}

	public static void displayUnimplementedMessage() {
		JOptionPane.showMessageDialog(null,
				"The functionality is not implemented yet." + "Our developers\nare working hard on it! Stay tuned!",
				"Sorry -- Unimplemented", JOptionPane.INFORMATION_MESSAGE);
	}

	protected static void displayWelcomeFrame() {
		System.out.println("Welcome to Gomoku Plus! Not a command line application anymore :)");
		JFrame frame = new JFrame("Gomoku Plus");
		frame.setSize(AbstractGame.defaultFrameDimension);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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
		JPanel btnPanel = new JPanel();
		frame.add(btnPanel);
		JButton singleplayerBtn = getPlainLookbtn("Singleplayer", "Open Sans", 28, Font.PLAIN, Color.CYAN);
		JButton multiplayerBtn = getPlainLookbtn("Multiplayer", "Open Sans", 28, Font.PLAIN, Color.YELLOW);
		JButton networkBtn = getPlainLookbtn("Network", "Open Sans", 28, Font.PLAIN, Color.RED);
		JButton aiGameBtn = getPlainLookbtn("AI Game", "Open Sans", 28, Font.PLAIN, Color.GRAY);
		JButton optionsBtn = getPlainLookbtn("Options", "Open Sans", 28, Font.PLAIN, Color.WHITE);
		JButton experimentalBtn = getPlainLookbtn("Experiments", "Open Sans", 28, Font.PLAIN, Color.PINK);
		btnPanel.add(singleplayerBtn);
		btnPanel.add(multiplayerBtn);
		btnPanel.add(networkBtn);
		btnPanel.add(aiGameBtn);
		btnPanel.add(optionsBtn);
		btnPanel.add(experimentalBtn);
		machineLearningSetup();

		singleplayerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popSinglePlayerGameOptionWindow(frame);
			}
		});

		multiplayerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayUnimplementedMessage();
			}
		});

		networkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayUnimplementedMessage();
			}
		});

		aiGameBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popAIGameOptionWindow(frame);
			}
		});

		optionsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				displayUnimplementedMessage();
			}
		});

		experimentalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UnrestrictedCvCGame experiment = new UnrestrictedCvCGame(new UnrestrictedBoard(),
                        Difficulty.custom, Difficulty.custom);
                experiment.setCustomAIParams(3, 0);
                experiment.runCvCGameForRecord(10000);
            }
        });
	}

	public static void machineLearningSetup() {
		Zobrist.generateSeeds();
		List<Integer> resultList = new ArrayList<>();
		List<List<Integer>> historicalGames = LocalStorage.getAllPreviousGames(resultList);
		BoardTree.statMap = Zobrist.getStatMap(historicalGames, resultList);
		BoardTree.cachedLocs = LocalStorage.readCritLocs();
	}

	protected static JButton getPlainLookbtn(String displayText, String font, int fontSize, int fontStyle,
			Color color) {
		JButton btn = new JButton(displayText);
		btn.setBackground(color);
		btn.setFont(new Font(font, fontStyle, fontSize));
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		return btn;
	}
	
	private static void popAIGameOptionWindow(JFrame welcomeFrame) {
		JFrame optionWindow = new JFrame("AI Options");
		optionWindow.setVisible(true);
		optionWindow.setSize(560, 720);
		JPanel optionPanel = new JPanel();
		BoxLayout layout = new BoxLayout(optionPanel, BoxLayout.Y_AXIS);
		optionPanel.setLayout(layout);
		optionWindow.add(optionPanel);
		optionPanel.setBorder(new EmptyBorder(20, 5, 20, 5));
		JLabel titleLabel = new JLabel("Game Options");
		titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 48));
		optionPanel.add(titleLabel);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		UIManager.put("RadioButton.font", radioBtnsFont);
		JLabel blackDiff = new JLabel("Choose black player level");
		blackDiff.setFont(panelSubTitleFont);
		JLabel whiteDiff = new JLabel("Choose white player level");
		whiteDiff.setFont(panelSubTitleFont);
		JRadioButton noviceDiffOption = new JRadioButton("Novice");
		JRadioButton intermediateDiffOption = new JRadioButton("Intermediate");
		JRadioButton advancedDiffOption = new JRadioButton("Advanced (slow)");
		JRadioButton ultimateDiffOption = new JRadioButton("Ultimate (very slow)");
		JRadioButton noviceForWhite = new JRadioButton("Novice");
		JRadioButton intermediateForWhite = new JRadioButton("Intermediate");
		JRadioButton advancedForWhite = new JRadioButton("Advanced (slow)");
		JRadioButton ultimateForWhite = new JRadioButton("Ultimate (very slow)");
		
		JPanel blackPanel = new JPanel();
		blackPanel.add(noviceDiffOption);
		blackPanel.add(intermediateDiffOption);
		blackPanel.add(advancedDiffOption);
		blackPanel.add(ultimateDiffOption);
		JPanel whitePanel = new JPanel();
		whitePanel.add(noviceForWhite);
		whitePanel.add(intermediateForWhite);
		whitePanel.add(advancedForWhite);
		whitePanel.add(ultimateForWhite);
		optionPanel.add(blackDiff);
		optionPanel.add(blackPanel);
		optionPanel.add(whiteDiff);
		optionPanel.add(whitePanel);
		blackDiff.setAlignmentX(Component.CENTER_ALIGNMENT);
		whiteDiff.setAlignmentX(Component.CENTER_ALIGNMENT);
		noviceDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		noviceForWhite.setAlignmentX(Component.CENTER_ALIGNMENT);
		intermediateDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		intermediateForWhite.setAlignmentX(Component.CENTER_ALIGNMENT);
		advancedDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		advancedForWhite.setAlignmentX(Component.CENTER_ALIGNMENT);
		ultimateDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		ultimateForWhite.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		ButtonGroup blackGroup = new ButtonGroup();
		blackGroup.add(noviceDiffOption);
		blackGroup.add(intermediateDiffOption);
		blackGroup.add(advancedDiffOption);
		blackGroup.add(ultimateDiffOption);
		ButtonGroup whiteGroup = new ButtonGroup();
		whiteGroup.add(noviceForWhite);
		whiteGroup.add(intermediateForWhite);
		whiteGroup.add(advancedForWhite);
		whiteGroup.add(ultimateForWhite);
		intermediateDiffOption.setSelected(true);
		advancedForWhite.setSelected(true);
		
		
		JButton playButton = Main.getPlainLookbtn("Play!", "Calibri", 33, Font.PLAIN, Color.MAGENTA);
		optionPanel.add(Box.createVerticalStrut(20));
		optionPanel.add(playButton);
		playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		playButton.setMargin(new Insets(0, 50, 0, 50));
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Difficulty firstDiff = null;
				Difficulty secondDiff = null;
				if (noviceDiffOption.isSelected())
					firstDiff = Difficulty.novice;
				else if (intermediateDiffOption.isSelected())
					firstDiff = Difficulty.intermediate;
				else if (advancedDiffOption.isSelected())
					firstDiff = Difficulty.advanced;
				else
					firstDiff = Difficulty.ultimate;
				
				if (noviceForWhite.isSelected())
					secondDiff = Difficulty.novice;
				else if (intermediateForWhite.isSelected())
					secondDiff = Difficulty.intermediate;
				else if (advancedForWhite.isSelected())
					secondDiff = Difficulty.advanced;
				else
					secondDiff = Difficulty.ultimate;
				
				optionWindow.dispose();
				welcomeFrame.dispose();
				new UnrestrictedCvCGame(firstDiff, secondDiff);
			}
		});
	}

	private static void popSinglePlayerGameOptionWindow(JFrame welcomeFrame) {
		JFrame singlePlayerOptionFrame = new JFrame("Options");
		singlePlayerOptionFrame.setVisible(true);
		singlePlayerOptionFrame.setSize(560, 720);
		JPanel singlePlayerOptionPanel = new JPanel();
		BoxLayout optionLayout = new BoxLayout(singlePlayerOptionPanel, BoxLayout.Y_AXIS);
		singlePlayerOptionPanel.setLayout(optionLayout);

		singlePlayerOptionFrame.add(singlePlayerOptionPanel);
		singlePlayerOptionPanel.setBorder(new EmptyBorder(20, 5, 20, 5));
		JLabel titleLabel = new JLabel("Game Options");
		titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 48));
		singlePlayerOptionPanel.add(titleLabel);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		UIManager.put("RadioButton.font", radioBtnsFont);
		JLabel chooseTurn = new JLabel("Choose your turn");
		chooseTurn.setFont(panelSubTitleFont);
		JLabel chooseDiff = new JLabel("Choose your difficulty");
		chooseDiff.setFont(panelSubTitleFont);
		JRadioButton senteOption = new JRadioButton("Always First");
		JRadioButton goteOption = new JRadioButton("Always Second");
		JRadioButton randomOption = new JRadioButton("Random");
		JRadioButton takeTurnOption = new JRadioButton("Alternate");
		JRadioButton noviceDiffOption = new JRadioButton("Novice");
		JRadioButton intermediateDiffOption = new JRadioButton("Intermediate");
		JRadioButton advancedDiffOption = new JRadioButton("Advanced (slow)");
		JRadioButton ultimateDiffOption = new JRadioButton("Ultimate (very slow)");
		JRadioButton mysteriousButton = new JRadioButton("Mysterious");
		JRadioButton bogo = new JRadioButton("Even novice is too hard");
		singlePlayerOptionPanel.add(chooseTurn);
		chooseTurn.setAlignmentX(Component.CENTER_ALIGNMENT);
		goteOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		randomOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		takeTurnOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		senteOption.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel turnOptionPanel = new JPanel();
		turnOptionPanel.add(senteOption);
		turnOptionPanel.add(goteOption);
		turnOptionPanel.add(randomOption);
		turnOptionPanel.add(takeTurnOption);
		singlePlayerOptionPanel.add(turnOptionPanel);

		JPanel diffOptionPanel = new JPanel();
		diffOptionPanel.add(noviceDiffOption);
		diffOptionPanel.add(intermediateDiffOption);
		diffOptionPanel.add(advancedDiffOption);
		diffOptionPanel.add(ultimateDiffOption);
		diffOptionPanel.add(mysteriousButton);
		diffOptionPanel.add(bogo);
		singlePlayerOptionPanel.add(chooseDiff);
		singlePlayerOptionPanel.add(diffOptionPanel);
		chooseDiff.setAlignmentX(Component.CENTER_ALIGNMENT);
		noviceDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		intermediateDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		advancedDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		ultimateDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		mysteriousButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		// create a group so that only one difficulty level is selected
		ButtonGroup difficultyGroup = new ButtonGroup();
		difficultyGroup.add(noviceDiffOption);
		difficultyGroup.add(intermediateDiffOption);
		difficultyGroup.add(advancedDiffOption);
		difficultyGroup.add(ultimateDiffOption);
		difficultyGroup.add(mysteriousButton);
		difficultyGroup.add(bogo);

		// select two default things.
		intermediateDiffOption.setSelected(true);
		senteOption.setSelected(true);

		// create a group so that only one turn is selected
		ButtonGroup turnGroup = new ButtonGroup();
		turnGroup.add(senteOption);
		turnGroup.add(goteOption);
		turnGroup.add(randomOption);
		turnGroup.add(takeTurnOption);
		randomOption.setSelected(true);

		JButton playButton = Main.getPlainLookbtn("Play!", "Calibri", 33, Font.PLAIN, Color.CYAN);
		singlePlayerOptionPanel.add(Box.createVerticalStrut(20));
		singlePlayerOptionPanel.add(playButton);
		playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		playButton.setMargin(new Insets(0, 50, 0, 50));
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int turnPolicy = 0;
				if (senteOption.isSelected())
					turnPolicy = AbstractGame.player_always_black;
				else if (goteOption.isSelected())
					turnPolicy = AbstractGame.player_always_white;
				else if (randomOption.isSelected())
					turnPolicy = AbstractGame.random_turn;
				else
					turnPolicy = AbstractGame.alternating_turn;
				
				// TODO get user's choices
				singlePlayerOptionFrame.dispose();
				if (noviceDiffOption.isSelected())
					game = new UnrestrictedGame(turnPolicy, Difficulty.novice);
				else if (intermediateDiffOption.isSelected())
					game = new UnrestrictedGame(turnPolicy, Difficulty.intermediate);
				else if (advancedDiffOption.isSelected())
					game = new UnrestrictedGame(turnPolicy, Difficulty.advanced);
				else if (ultimateDiffOption.isSelected())
					game = new UnrestrictedGame(turnPolicy, Difficulty.ultimate);
				else if (mysteriousButton.isSelected()) {
					game = new UnrestrictedGame(turnPolicy, Difficulty.custom);
				}
				else {
					// For "Even novice is too hard", we use random XDDDD
					int randNum = new Random().nextInt(4);
					if (randNum == 0)
						game = new UnrestrictedGame(turnPolicy, Difficulty.novice);
					else if (randNum == 1)
						game = new UnrestrictedGame(turnPolicy, Difficulty.intermediate);
					else if (randNum == 2)
						game = new UnrestrictedGame(turnPolicy, Difficulty.advanced);
					else
						game = new UnrestrictedGame(turnPolicy, Difficulty.ultimate);
				}
				welcomeFrame.dispose();
			}
		});
	}
}
