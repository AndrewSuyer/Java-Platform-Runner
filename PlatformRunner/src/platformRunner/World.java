package platformRunner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A {@code World} is a collection of {@code Level}s that are similar in theme. 
 * 
 * @author Andrew Suyer
 */
public class World extends JPanel {
	
	private Color backgroundColor;
	
	/** Label which says the current world number */
	private JLabel worldTitle;
	
	private JSeparator worldTitleUnderline;
	
	/** Buttons which, when pressed, begin the level cooresponding to their index */
	private JButton[] levelSelectionButtons;
	
	/** The {@code Level}s that make up this world */
	private Level[] levels;
	
	/**
	 * Creates a {@code World} with a specified background color, which should match the theme of its {@code Level}s, and
	 * the number of the world
	 * @param bc - background color
	 * @param worldNum - this world number
	 */
	public World (Color bc, int worldNum) {
		
		// Initializing instance variables:
		backgroundColor = bc;
		levelSelectionButtons = new JButton[4];
		worldTitle = new JLabel("World " + worldNum, JLabel.CENTER);
		worldTitleUnderline = new JSeparator(JSeparator.HORIZONTAL);
		levels = new Level[4];
		
		// Setting world title properties:
		worldTitle.setSize(370, 75);
		worldTitle.setLocation(GameFrame.frameWidth / 2 - 370 / 2, 32);
		worldTitle.setFont(new Font("Serif", Font.BOLD, 100));
		add(worldTitle);
		
		// Setting world title underline properties:
		worldTitleUnderline.setLocation(GameFrame.frameWidth / 2 - 370 / 2, 132);
		worldTitleUnderline.setSize(370, 5);
		worldTitleUnderline.setBackground(Color.black);
		add(worldTitleUnderline);
		
		// Creating each button and adding it to the panel:
		// When button is pressed, all elements of this panel are removed and the cooresponding level is added to the panel
		levelSelectionButtons[0] = new JButton("Level 1");
		levelSelectionButtons[0].setSize(GameFrame.frameWidth/2 - 64, GameFrame.frameHeight/4 - 64);
		levelSelectionButtons[0].setLocation(32, 256);
		levelSelectionButtons[0].setFont(new Font("Serif", Font.BOLD, 50));
		levelSelectionButtons[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand());
				removeAll();
				add(levels[0]);
				levels[0].startThread();
				update(getGraphics());
			}
		});
		add(levelSelectionButtons[0]);
		
		levelSelectionButtons[1] = new JButton("Level 2");
		levelSelectionButtons[1].setSize(GameFrame.frameWidth/2 - 64, GameFrame.frameHeight/4 - 64);
		levelSelectionButtons[1].setLocation(GameFrame.frameWidth/2 + 32, 256);
		levelSelectionButtons[1].setFont(new Font("Serif", Font.BOLD, 50));
		levelSelectionButtons[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand());
				removeAll();
				add(levels[1]);
				levels[1].startThread();
				update(getGraphics());
			}
		});
		add(levelSelectionButtons[1]);
		
		levelSelectionButtons[2] = new JButton("Level 3");
		levelSelectionButtons[2].setSize(GameFrame.frameWidth/2 - 64, GameFrame.frameHeight/4 - 64);
		levelSelectionButtons[2].setLocation(32, 512);
		levelSelectionButtons[2].setFont(new Font("Serif", Font.BOLD, 50));
		levelSelectionButtons[2].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand());
				removeAll();
				add(levels[2]);
				levels[2].startThread();
				update(getGraphics());
			}
		});
		add(levelSelectionButtons[2]);
		
		levelSelectionButtons[3] = new JButton("Level 4");
		levelSelectionButtons[3].setSize(GameFrame.frameWidth/2 - 64, GameFrame.frameHeight/4 - 64);
		levelSelectionButtons[3].setLocation(GameFrame.frameWidth/2 + 32, 512);
		levelSelectionButtons[3].setFont(new Font("Serif", Font.BOLD, 50));
		levelSelectionButtons[3].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand());
				removeAll();
				add(levels[3]);
				levels[3].startThread();
				update(getGraphics());
			}
		});
		add(levelSelectionButtons[3]);
		
		// Panel setup:
		setSize(GameFrame.frameWidth, GameFrame.frameHeight);
		setBackground(backgroundColor);
		setLayout(null);
		setVisible(true);
	}
	
	/**
	 * Adds a {@code Level} to this {@code World}
	 * @param levelNumber - which level is being added
	 * @param l - the level to be added
	 */
	public void addLevel (int levelNumber, Level l) {
		levels[levelNumber - 1] = l;
	}
	
	private static final long serialVersionUID = 1L;
}
