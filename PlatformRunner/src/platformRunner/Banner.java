package platformRunner;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Banner extends JPanel {
	
	public static final int bannerWidth = 480;
	public static final int bannerHeight = 288;
	
	private static final ImageIcon border = new ImageIcon("D:\\\\Gimp\\\\PlatformRunnerTextures\\\\banner_border.png");
	
	
	
	public Banner (Type bannerType) {
		
		// Panel setup:
		setSize(bannerWidth, bannerHeight);
		setLayout(null);
		
		// Panel border (used for all banner types):
		JLabel borderLabel = new JLabel(border);
		borderLabel.setSize(bannerWidth, bannerHeight);
		add(borderLabel);
		
		switch (bannerType) {
		case LEVEL_INDICATOR:
			
			break;
		case DEATH_SCREEN:
			
			break;
		case LEVEL_COMPLETION:
			
			break;
		}
		
		setVisible(true);
	}
	
	/**
	 * The type of {@code Banner} this is. A {@code Banner} can be a level indicator, a death screen, or a level completion
	 * banner. More information about Banner types embedded within the constant. 
	 * @author Andrew Suyer
	 */
	public static enum Type {
		
		/** Placed at the start of a {@code Level} to let the player know what level they are playing */
		LEVEL_INDICATOR,
		
		/** Appears when the player dies. Includes a retry and quit buttons */
		DEATH_SCREEN,
		
		/** Appears when the player finishes a {@code Level}. Tells player how long they took to complete the level. Includes
		 * world select and next level buttons */
		LEVEL_COMPLETION;
	}

	private static final long serialVersionUID = 1L;
}
