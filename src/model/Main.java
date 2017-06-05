package model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import model.AbstractGame.Difficulty;

public class Main {
	private static AbstractGame game;
	private static final Font panelSubTitleFont = new Font("Tahoma", Font.PLAIN, 35);
	private static final Font radioBtnsFont = new Font("Calibri", Font.PLAIN, 32);
	
	public static void main(String args[]) {
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
		AbstractGame.addCloseConfirmation(frame);
		JPanel btnPanel = new JPanel();
		frame.add(btnPanel);
		JButton singleplayerBtn = getPlainLookbtn("Singleplayer", "Open Sans", 28, Font.PLAIN, Color.CYAN);
		JButton multiplayerBtn = getPlainLookbtn("Multiplayer", "Open Sans", 28, Font.PLAIN, Color.YELLOW);
		JButton networkBtn = getPlainLookbtn("Network", "Open Sans", 28, Font.PLAIN, Color.RED);
		JButton aiGameBtn = getPlainLookbtn("AI Game", "Open Sans", 28, Font.PLAIN, Color.GRAY);
		JButton optionsBtn = getPlainLookbtn("Options", "Open Sans", 28, Font.PLAIN, Color.WHITE);
		btnPanel.add(singleplayerBtn);
		btnPanel.add(multiplayerBtn);
		btnPanel.add(networkBtn);
		btnPanel.add(aiGameBtn);
		btnPanel.add(optionsBtn);

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
				game = new UnrestrictedCvCGame();
				frame.dispose();
			}
		});

		optionsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				displayUnimplementedMessage();
			}
		});
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
		// TODO make this thing look nicer!!!!!!
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

		new JSeparator(SwingConstants.HORIZONTAL);
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
				if (senteOption.isSelected()) {
				}
				if (noviceDiffOption.isSelected())
					game = new UnrestrictedGame(turnPolicy, Difficulty.novice);
				else if (intermediateDiffOption.isSelected())
					game = new UnrestrictedGame(turnPolicy, Difficulty.intermediate);
				else if (advancedDiffOption.isSelected())
					game = new UnrestrictedGame(turnPolicy, Difficulty.advanced);
				else if (ultimateDiffOption.isSelected())
					game = new UnrestrictedGame(turnPolicy, Difficulty.ultimate);
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
