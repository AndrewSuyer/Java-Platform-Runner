package platformRunner;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/** 
 * The {@code Player} is the sprite that is controlled by the user. A {@code Player} has many
 * textures representing its many states which are standing, jumping, squatting, and moving. 
 * A {@code Player} stores its precise x and y position on the {@code Level} board. 
 * The player has specific stats like max horizontal speed, jump height, and horizontal acceleration. 
 * The player updates its own position and state using its defined methods
 * @author Andrew Suyer
 */
public class Player extends JLabel {
	
	/** The player sprite texture while standing still */
	private ImageIcon standingTexture;
	
	/** The player sprite textrure while jumping in the air */
	private ImageIcon jumpingTexture;
	
	/** The player sprite texture while squatting on the ground */
	private ImageIcon squattingTexture;
	
	/** The player sprite textures while it is moving right */
	private ImageIcon[] movingRightTextures;
	
	/** The player sprite textures while it is moving left */
	private ImageIcon[] movingLeftTextures;
	
	/** Index of current moving right texture that is displayed */
	private byte currentMovingRightTextureIndex;
	
	/** Index of current moving left texture that is displayed */
	private byte currentMovingLeftTextureIndex;
	
	/** How times this players moving texture is updated per second */
	private static final int movingTextureUpdateFrequency = 5;
	
	/**
	 * Displays the next player moving texture according to {@code movingTextureUpdateFrequency} and according to the direction that the player
	 * is moving
	 */
	public void nextTexture (int currentFrame) {
		if (currentFrame % (GameFrame.framesPerSecond / movingTextureUpdateFrequency) == 0) {		// if on an update frame
			if (xVelocity > 0) {
				if (currentMovingRightTextureIndex == movingRightTextures.length - 1) {
					setIcon(movingRightTextures[0]);
					currentMovingRightTextureIndex = 0;
				} else {
					setIcon(movingRightTextures[currentMovingRightTextureIndex + 1]);
					currentMovingRightTextureIndex++;
				}
			} else {
				if (currentMovingLeftTextureIndex == movingLeftTextures.length - 1) {
					setIcon(movingLeftTextures[0]);
					currentMovingLeftTextureIndex = 0;
				} else {
					setIcon(movingLeftTextures[currentMovingLeftTextureIndex + 1]);
					currentMovingLeftTextureIndex++;
				}
			}
			
		}
		
	}
	
	/**
	 * Sets the player sprite texture to the standing texure
	 */
	public void standingTexture () {
		setIcon(standingTexture);
	}
	
	/**
	 * Sets the player sprite texture to the jumping texure
	 */
	public void jumpingTexture () {
		setIcon(jumpingTexture);
	}
	
	/**
	 * Sets the player sprite texture to the squatting texture
	 */
	public void squattingTexture () {
		setIcon(squattingTexture);
	}
	
	public final double maxWalkingSpeed = 4;
	
	public final double maxRunningSpeed = 4;
	
	public final double maxJumpHeight = 3.5;
	
	/** Current horizontal position of the player on the {@code levelBoard} in blocks */
	private double xPosition;
	
	public void setXPosition (double x) { xPosition = x; }
	public double getXPosition () { return xPosition; }
	
	/** Current vertical position of the player on the {@code levelBoard} in blocks */
	private double yPosition;
	
	public void setYPosition (double y) { yPosition = y; }
	public double getYPosition () { return yPosition; }
	
	/** The current horizontal velocity of the player measured in blocks per second */
	private double xVelocity;
	
	public void setXVelocity (double xv) { xVelocity = xv; }
	public double getXVelocity () { return xVelocity; }
	
	/** The current vertical velocity of the player measured in blocks per second */
	private double yVelocity;
	
	public void setYVelocity (double yv) { yVelocity = yv; }
	public double getYVelocity () { return yVelocity; }
	
	/** The magnitude of the acceleration that the player has when speeding up measured
	 * in blocks per second per second */
	public final double xAcceleration = 5.0;
	
	/**
	 * Creates a Player that is placed at the position (xStart, yStart) when the level begins. The player begins at rest.
	 * @param scale - block scale factor
	 * @param xStart - starting x position
	 * @param yStart - starting y position
	 */
	public Player (int scale, int xStart, int yStart) {
		
		// Initializing instance variables and textures:
		standingTexture = new ImageIcon("D:\\\\Gimp\\\\New player texture\\\\player_standing.png");
		standingTexture = new ImageIcon(standingTexture.getImage().getScaledInstance(Block.defaultBlockResolution * scale, Block.defaultBlockResolution * scale, Image.SCALE_SMOOTH));		// scaling texture
		
		jumpingTexture = new ImageIcon("D:\\\\Gimp\\\\New player texture\\\\player_jumping.png");
		jumpingTexture = new ImageIcon(jumpingTexture.getImage().getScaledInstance(Block.defaultBlockResolution * scale, Block.defaultBlockResolution * scale, Image.SCALE_SMOOTH));
		
		squattingTexture = new ImageIcon("D:\\\\Gimp\\\\New player texture\\\\player_squatting.png");
		squattingTexture = new ImageIcon(squattingTexture.getImage().getScaledInstance(Block.defaultBlockResolution * scale, Block.defaultBlockResolution * scale, Image.SCALE_SMOOTH));
		
		movingRightTextures = new ImageIcon[2];
		movingRightTextures[0] = new ImageIcon("D:\\\\Gimp\\\\New player texture\\\\player_moving_right_1.png");
		movingRightTextures[0] = new ImageIcon(movingRightTextures[0].getImage().getScaledInstance(Block.defaultBlockResolution * scale, Block.defaultBlockResolution * scale, Image.SCALE_SMOOTH));
		movingRightTextures[1] = new ImageIcon("D:\\\\Gimp\\\\New player texture\\\\player_moving_right_2.png");
		movingRightTextures[1] = new ImageIcon(movingRightTextures[1].getImage().getScaledInstance(Block.defaultBlockResolution * scale, Block.defaultBlockResolution * scale, Image.SCALE_SMOOTH));
		
		movingLeftTextures = new ImageIcon[2];
		movingLeftTextures[0] = new ImageIcon("D:\\\\Gimp\\\\New player texture\\\\player_moving_left_1.png");
		movingLeftTextures[0] = new ImageIcon(movingLeftTextures[0].getImage().getScaledInstance(Block.defaultBlockResolution * scale, Block.defaultBlockResolution * scale, Image.SCALE_SMOOTH));
		movingLeftTextures[1] = new ImageIcon("D:\\\\Gimp\\\\New player texture\\\\player_moving_left_2.png");
		movingLeftTextures[1] = new ImageIcon(movingLeftTextures[1].getImage().getScaledInstance(Block.defaultBlockResolution * scale, Block.defaultBlockResolution * scale, Image.SCALE_SMOOTH));
		
		currentMovingRightTextureIndex = 0;
		currentMovingLeftTextureIndex = 0;
		
		xPosition = xStart;
		yPosition = yStart;
		xVelocity = 0;
		yVelocity = 0;
		
		// Label setup
		setIcon(jumpingTexture);
		setSize(Block.defaultBlockResolution * scale, Block.defaultBlockResolution * scale);
		setVisible(true);
		
	}
	
	/**
	 * Updates position of this {@code Player} on the {@code Level} panel to the nearest integer value for the current actual position
	 * of the player
	 * @param sf - block scale factor
	 */
	public void updatePosition (int sf) {
		setLocation((int) (xPosition * sf * Block.defaultBlockResolution), (int) (yPosition * sf * Block.defaultBlockResolution));
	}

	private static final long serialVersionUID = 1L;

}
