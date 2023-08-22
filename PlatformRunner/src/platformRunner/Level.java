package platformRunner;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * One {@code Level} is displayed on the screen at a time, and when the player completes the level, the 
 * next level is displayed. Each level is represented by a 2D array of {@code Block}s. When a {@code Level} 
 * is in action, a game loop runs which handles the movement of the level panel, as well as the movement of 
 * the player. The speed that the level panel moves at is given by {@code levelSpeed} and is measured in blocks 
 * per second. 
 * 
 * @author Andrew Suyer
 */
public class Level extends JPanel implements Runnable {
	
	private Color backgroundColor;
	
	/** The grid of Blocks that make up this level */
	private Block[][] levelBoard;
	
	/** The factor by witch the resolution of the 16x16 block textures are scaled */
	public final int blockScaleFactor;
	
	public final int panelPixelWidth;		// Width and height of panel in pixels. Calculated in constructor
	public final int panelPixelHeight;
	
	/** How fast this level will move in blocks per second */
	public final double levelSpeed;
	
	/** The horizontal pixel position of the panel */
	private double panelPosition;
	
	/** The acceleration due to gravity for this level */
	public final double gravitationalAcceleration;
	
	/** Thread that controls all movement using a game loop */
	private Thread gameThread;
	
	private Player player;
	
	/** Maps keyboard inputs to movement actions which are up, down, left, and right */
	private InputMap inputMap;
	
	/** Maps movement actions to events which update the values of {@code keysPressed} */
	private ActionMap actionMap;
	
	/**
	 * The keys that are currently pressed. 0 = up, 1 = right, 2 = down, 3 = left. Follows compass
	 * directions, rotating clockwise.
	 */
	private boolean[] keysPressed;
	
	/** Counts the number of frames the player has been in the finish area for. When value excedes the game FPS, the level is over.
	 * This is the equivilant of the player being in the finish area for 1 second */
	private int framesPlayerIsInFinishArea;
	
	public final int levelNumber;
	
	private Banner levelNumberBanner;
	
	/** Counts how many times the player has died on this level */
	private int playerDeathCounter;
	
	private final Point playerStartPosition;
	
	/** The currently displayed frame during this second */
	private int currentFrame;
	
	public int getCurrentFrame () { return currentFrame; }
	
	
	/**
	 * Creates a level with all these specified parameters. Note: once a level is created, a method should be
	 * made to place Blocks into the {@code levelBoard} using {@code setLevelBoard()} and {@code putBlocksOnPanel()}
	 * @param bc - Panel background color
	 * @param scale - Block scale factor
	 * @param w - levelBoard width, in terms of Blocks and not pixels
	 * @param h - levelBoard height, in terms of Blocks and not pixels
	 * @param speed - How fast the level moves in blocks per second
	 * @param gAcc - Accleration due to gravity for this level
	 * @param playerXStart - the starting x position for the player
	 * @param playerYStart - the starting y position for the player
	 * @param levelNum - the level number
	 */
	public Level (Color bc, int scale, int w, int h, double speed, double gAcc, int playerXStart, int playerYStart, int levelNum) {
		
		// Initializing instance variables:
		backgroundColor = bc;
		blockScaleFactor = scale;
		levelBoard = new Block[w][h];
		panelPixelWidth = w * Block.defaultBlockResolution * blockScaleFactor;
		panelPixelHeight = h * Block.defaultBlockResolution * blockScaleFactor;
		levelSpeed = speed;
		panelPosition = 0;
		gravitationalAcceleration = gAcc;
		gameThread = new Thread(this);					// Thread using this as Runnable target
		player = new Player(blockScaleFactor, playerXStart, playerYStart);
		framesPlayerIsInFinishArea = 0;
		levelNumber = levelNum;
		levelNumberBanner = new Banner(Banner.Type.LEVEL_INDICATOR);
		playerDeathCounter = 0;
		playerStartPosition = new Point(playerXStart, playerYStart);
		currentFrame = 0;
		
		add(player);
		player.updatePosition(blockScaleFactor);
		
		createInputAndActionMap();
		
		// Panel setup:
		setSize(panelPixelWidth, panelPixelHeight);
		setBackground(backgroundColor);
		setLayout(null);
		setLocation(0, -39);						// panel shifted up 39 pixels so bottom of bottom block lines up with bottom of screen
		
	}
	
	/**
	 * Defines the keybinds for player movement and assigns movement actions to them using the {@code inputMap} and
	 * the {@code actionMap}
	 */
	private void createInputAndActionMap () {
		
		keysPressed = new boolean[4];
		
		// Setting up input and action map
		inputMap = player.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);		// try removing condition??
		actionMap = player.getActionMap();
		
		// Bind arrow key presses to actions
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		
		// Bind key releases to actions
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "stop left");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "stop right");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "stop up");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "stop down");
		
		// Define actions for arrow key presses and releases
		actionMap.put("left", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				keysPressed[3] = true;
			}
			
			private static final long serialVersionUID = 1L;
		});
		
		actionMap.put("stop left", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				keysPressed[3] = false;
			}
			
			private static final long serialVersionUID = 1L;
		});
		
		actionMap.put("right", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				keysPressed[1] = true;
			}
			
			private static final long serialVersionUID = 1L;
		});
		
		actionMap.put("stop right", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				keysPressed[1] = false;
			}
			
			private static final long serialVersionUID = 1L;
		});
		
		actionMap.put("up", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				keysPressed[0] = true;
			}
			
			private static final long serialVersionUID = 1L;
		});
		
		actionMap.put("stop up", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				keysPressed[0] = false;
			}
			
			private static final long serialVersionUID = 1L;
		});
		
		actionMap.put("down", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				keysPressed[2] = true;
			}
			
			private static final long serialVersionUID = 1L;
		});
		
		actionMap.put("stop down", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				keysPressed[2] = false;
			}
			
			private static final long serialVersionUID = 1L;
		});
	}
	
	/**
	 * Returns true of the block to the right of the player is either solid or breakable, and false otherwise. Checks the block
	 * to the right of the two right corners of the players hitbox. Used for determining if the player has collided with a block 
	 * to the right. Also returns false if the block to the right 
	 * @return true if a block to the right is solid or breakable, and false otherwise
	 */
	private boolean solidOrBreakableBlockToTheRight () {
		boolean blockTopRight = false;
		boolean blockBottomRight = false;
		if (levelBoard[(int) (player.getXPosition() + 1)][(int) player.getYPosition()] != null) {
			blockTopRight = levelBoard[(int) player.getXPosition() + 1][(int) player.getYPosition()].getBlockId() % 6 == 0 ||		// block right of top-right corner is solid
							levelBoard[(int) player.getXPosition() + 1][(int) player.getYPosition()].getBlockId() % 6 == 1;			// block right of top-right corner is breakable
		}
		if (levelBoard[(int) player.getXPosition() + 1][(int) (player.getYPosition() + 1 - 1.0 / Block.defaultBlockResolution)] != null) {
			blockBottomRight = levelBoard[(int) player.getXPosition() + 1][(int) (player.getYPosition() + 1 - 1.0 / Block.defaultBlockResolution)].getBlockId() % 6 == 0 ||		// block right of bottom-right corner is solid
								levelBoard[(int) player.getXPosition() + 1][(int) (player.getYPosition() + 1 - 1.0 / Block.defaultBlockResolution)].getBlockId() % 6 == 1;		// block right or bottom-right corner is breakable
		}
		return blockTopRight || blockBottomRight;
	}
	
	/**
	 * Returns true of the block to the right of the player is either solid or breakable, and false otherwise. Checks the block(s)
	 * to the right of the two right corners of the players hitbox. Used for determining if the player has collided with a block 
	 * to the right. Also returns false if the block to the right is null (air)
	 * @return true if a block to the right is solid or breakable, and false otherwise
	 */
	private boolean solidOrBreakableBlockToTheLeft () {
		boolean blockTopLeft = false;
		boolean blockBottomLeft = false;
		if (levelBoard[(int) (player.getXPosition() - 1.0 / Block.defaultBlockResolution)][(int) player.getYPosition()] != null) {
			blockTopLeft = levelBoard[(int) (player.getXPosition() - 1.0 / Block.defaultBlockResolution)][(int) player.getYPosition()].getBlockId() % 6 == 0 ||		// block left of top-left corner is solid
						levelBoard[(int) (player.getXPosition() - 1.0 / Block.defaultBlockResolution)][(int) player.getYPosition()].getBlockId() % 6 == 1;			// block left of top-left corner is breakable
		}
		if (levelBoard[(int) (player.getXPosition() - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() + 1 - 1.0 / Block.defaultBlockResolution)] != null) {
			blockBottomLeft = levelBoard[(int) (player.getXPosition() - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() + 1 - 1.0 / Block.defaultBlockResolution)].getBlockId() % 6 == 0 ||	// block left of bottom-left corneris solid
							levelBoard[(int) (player.getXPosition() - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() + 1 - 1.0 / Block.defaultBlockResolution)].getBlockId() % 6 == 1;		// block left or bottom-left corner is breakable
		}
		return blockTopLeft || blockBottomLeft;
	}
	
	/**
	 * Returns true if the block above the player is solid, and false otherwise. Checks the block(s) above the top-left and top-right
	 * corners of the players hitbox. Used for determining if the player has collided with a solid block from above. 
	 * @return true if a block above is solid, and false otherwise
	 */
	private boolean solidBlockAbove () {
		boolean blockTopLeft = false;
		boolean blockTopRight = false;
		if (levelBoard[(int) player.getXPosition()][(int) (player.getYPosition() - 1.0 / Block.defaultBlockResolution)] != null) {
			blockTopLeft = levelBoard[(int) player.getXPosition()][(int) (player.getYPosition() - 1.0 / Block.defaultBlockResolution)].getBlockId() % 6 == 0;	// block above top-left corner is solid
		}
		if (levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() - 1.0 / Block.defaultBlockResolution)] != null) {
			blockTopRight = levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() - 1.0 / Block.defaultBlockResolution)].getBlockId() % 6 == 0;	// block above top-right corner is solid
		}
		return blockTopLeft || blockTopRight;
	}
	
	/**
	 * Returns true if the block above the player is breakable, and false otherwise. Checks the block(s) above the top-left and top-right
	 * corners of the players hitbox. Used for determining if the player has collided with a breakable block from above. 
	 * @return true if a block above is breakable, and false otherwise
	 */
	private boolean breakableBlockAbove () {
		boolean blockTopLeft = false;
		boolean blockTopRight = false;
		if (levelBoard[(int) player.getXPosition()][(int) (player.getYPosition() - 1.0 / Block.defaultBlockResolution)] != null) {
			blockTopLeft = levelBoard[(int) player.getXPosition()][(int) (player.getYPosition() - 1.0 / Block.defaultBlockResolution)].getBlockId() % 6 == 1;	// block above top-left corner is breakable
		}
		if (levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() - 1.0 / Block.defaultBlockResolution)] != null) {
			blockTopRight = levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() - 1.0 / Block.defaultBlockResolution)].getBlockId() % 6 == 1;	// block above top-right corner is breakable
		}
		return blockTopLeft || blockTopRight;
	}
	
	/**
	 * Returns true if the block below the player is solid or breakable, and false otherwise. Checks the block(s) below the bottom-left
	 * and bottom-right corners of the players hitbox. Used for determining if hte player has collided with a breakable block from above.
	 * @return true if a block below is solid or breakable, and false otherwise
	 */
	private boolean solidOrBreakableBlockBelow () {
		boolean blockBottomLeft = false;
		boolean blockBottomRight = false;
		if (levelBoard[(int) player.getXPosition()][(int) (player.getYPosition() + 1)] != null) {
			blockBottomLeft = levelBoard[(int) player.getXPosition()][(int) (player.getYPosition() + 1)].getBlockId() % 6 == 0 ||		// below bottom-left is solid
							levelBoard[(int) player.getXPosition()][(int) (player.getYPosition() + 1)].getBlockId() % 6 == 1;			// below bottom-left is breakable
		}
		if (levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() + 1)] != null) {
			blockBottomRight = levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() + 1)].getBlockId() % 6 == 0 ||	// below bottom-right is solid
							levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() + 1)].getBlockId() % 6 == 1;		// below bottom-right is breakable
		}
		return blockBottomLeft || blockBottomRight;
	}
	
	/**
	 * Starts the game thread for this level
	 */
	public void startThread() {
		gameThread.start();
	}
	
	private void putPlayerAtStartPosition () {
		int xStart = (int) playerStartPosition.getX();
		int yStart = (int) playerStartPosition.getY();
		player.setXPosition(xStart);
		player.setYPosition(yStart);
	}
	
	@Override
	public void run () {
		
		// Statistic counters:
		int executionCount = 0;			// Counts how many times the loop executes per second
		int frameCount = 0;				// Counts how many frames are displayed per second
		
		// Time trackers:
		long currentTime = System.nanoTime();
		long nextUpdateTime = currentTime + (1000000000 / GameFrame.framesPerSecond);	// time of next frame update
		
		long oneSecondStart = currentTime;		// The start time for the current second. Used to count statistics
		
		player.setYVelocity(-2);		// players starts falling in the air
		
		// Game loop:
		while (true) {
			
			executionCount++;
			
			if (System.nanoTime() >= nextUpdateTime) {		// Executes every frame
				
				// Update time trackers:
				currentTime = System.nanoTime();
				nextUpdateTime = currentTime + (1000000000 / GameFrame.framesPerSecond);
				frameCount++;
				currentFrame++;
				
				// Update position of level panel:
				double dx = (double) Block.defaultBlockResolution * blockScaleFactor * levelSpeed / GameFrame.framesPerSecond;
				panelPosition -= dx;
				if (!(-panelPosition >= panelPixelWidth - GameFrame.frameWidth))
					// if not at the end of the screen
					setLocation((int) (panelPosition), getY());
				
				// Deadly block detection:
				// Checks if any of the corners of the players hitbox are a deadly block (id % 6 == 3), but first need to make sure
				// the block isnt null to avoid a NullPointerException
				
				if (
					// Top left corner isnt air and is deadly, or...
					(levelBoard[(int) player.getXPosition()][(int) player.getYPosition()] != null && 
						levelBoard[(int) player.getXPosition()][(int) player.getYPosition()].getBlockId() % 6 == 3) ||
					// Top right corner isnt air and is deadly, or...
					(levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) player.getYPosition()] != null && 
						levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) player.getYPosition()].getBlockId() % 6 == 3) ||
					// Bottom left corner isnt air and is deadly, or...
					(levelBoard[(int) player.getXPosition()][(int) (player.getYPosition() + 1 - 1.0 / Block.defaultBlockResolution)] != null && 
						levelBoard[(int) player.getXPosition()][(int) (player.getYPosition() + 1 - 1.0 / Block.defaultBlockResolution)].getBlockId() % 6 == 3) || 
					// Bottom right corner isnt air and is deadly, or...
					(levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() + 1 - 1.0 / Block.defaultBlockResolution)] != null &&
						levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) (player.getYPosition() + 1 - 1.0 / Block.defaultBlockResolution)].getBlockId() % 6 == 3)) 
				{
					System.out.println("You died!");
					playerDeathCounter++;
					
					try {
						gameThread.join(1000);					// Stop thread for 1 second
					} catch (InterruptedException e) {}
					
					System.out.println("Deaths: " + playerDeathCounter);
					
					panelPosition = 0;				// reset level panel to starting position (0)
					putPlayerAtStartPosition();
					player.setYVelocity(-2);		// player starts with y velocity of -2
					player.setXVelocity(0);
					gameThread.run();				// restart thread at begininning to reset time trackers
				}
				
				// Update position of the player according to the keys that are pressed:
				
				try {
					
					// Updating y position of player
					
					if (!(solidBlockAbove() || breakableBlockAbove() || solidOrBreakableBlockBelow())) {
						
						// NOT a solid or breakable block above or below the player
						// Falling through the air (Freefall)
						player.jumpingTexture();
						double yVel = player.getYVelocity();
						yVel += gravitationalAcceleration / GameFrame.framesPerSecond;
						double deltaY = yVel / GameFrame.framesPerSecond + 0.5 * gravitationalAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);
						player.setYVelocity(yVel);
						player.setYPosition(player.getYPosition() + deltaY);
						player.updatePosition(blockScaleFactor);
						
					} 
					if (solidBlockAbove() || breakableBlockAbove()) {
						
						// Player hits block above hit
						
						// Correcting y position if player is partly in the block above
						if (player.getYPosition() % 1 > 0.5)
							player.setYPosition((int) player.getYPosition() + 1);		
						
						// Removing the block above if its breakable
						if (breakableBlockAbove()) {
							Block topLeft = levelBoard[(int) player.getXPosition()][(int) player.getYPosition() - 1];
							Block topRight = levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) player.getYPosition() - 1];
							// Remove block if its breakable and not already broken (null)
							if (topLeft != null && topLeft.getBlockId() % 6 == 1) {
								topLeft.setVisible(false);
								levelBoard[(int) player.getXPosition()][(int) player.getYPosition() - 1] = null;	
							}
							if (topRight != null && topRight.getBlockId() % 6 == 1) {
								topRight.setVisible(false);
								levelBoard[(int) (player.getXPosition() + 1 - 1.0 / Block.defaultBlockResolution)][(int) player.getYPosition() - 1] = null;
							}
						}
						
						// if player hits ceiling with velocity of 0, then give it a little bit of speed so that it doesnt stick to the ceiling
						if (player.getYVelocity() > -0.1) {
							player.setYVelocity(-0.5);
						}
						
						// Hitting a block above causes player to rebound off ceiling
						double yVel = -1 * player.getYVelocity();
						double deltaY = yVel / GameFrame.framesPerSecond + 0.5 * gravitationalAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);
						player.setYVelocity(yVel);
						player.setYPosition(player.getYPosition() + deltaY);
						player.updatePosition(blockScaleFactor);
						
					} 
					if (solidOrBreakableBlockBelow()) {
						
						// Player hits the ground
						if (player.getYVelocity() != 0) {	
							// set velocity to 0 and standing texture only if the velocity hasnt been set to 0 already
							player.setYVelocity(0);
							player.standingTexture();
						}
							
						
						// Correcting y position if player is partly in the block below
						if (player.getYPosition() % 1 < 0.5)
							player.setYPosition((int) player.getYPosition());
						
						if (keysPressed[2]) 			// If pressing down, squatt
							player.squattingTexture();
						
						if (keysPressed[0]) {			// If pressing up, jump
							player.jumpingTexture();
							double yVel = -1 * Math.sqrt(2 * gravitationalAcceleration * player.maxJumpHeight);
							player.setYVelocity(yVel);
							double deltaY = yVel / GameFrame.framesPerSecond - 0.5 * gravitationalAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);
							player.setYPosition(player.getYPosition() + deltaY);
							player.updatePosition(blockScaleFactor);
						}
					}
					
					// Updating x position of the player:
					
					if (player.getXVelocity() > 0 && !solidOrBreakableBlockToTheRight()) {		
						// If player is moving right and the block to the right is NOT solid or breakable
						
						if (solidOrBreakableBlockBelow())		// update running texture if player is on the ground
							player.nextTexture(currentFrame);
						
						if (keysPressed[1]) {
							// Right key pressed
							// Accelerate rightwarwd to max speed then travel at that speed
							double xVel = player.getXVelocity();
							double deltaX = 0;
							if ((xVel < player.maxWalkingSpeed)) {
								// if below the max speed, increase speed according to equations
								xVel += player.xAcceleration / GameFrame.framesPerSecond;			// v = at = a/f
								deltaX = xVel / GameFrame.framesPerSecond + 0.5 * player.xAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);		// dx = vt + 0.5at^2
								player.setXVelocity(xVel);
							} else
								// if above max speed, travel at constant speed
								deltaX = xVel / GameFrame.framesPerSecond;			// dx = vt	(when acceleration is 0)
							player.setXPosition(player.getXPosition() + deltaX);
							player.updatePosition(blockScaleFactor);
						} else {
							// Right key released
							// Decelerate back to a stop
							double xVel = player.getXVelocity();
							double deltaX = 0;
							if (xVel > 0) {
								// if moving, start decelerating to a stop
								xVel -= player.xAcceleration / GameFrame.framesPerSecond;			// v = at = a/f
								deltaX = xVel / GameFrame.framesPerSecond - 0.5 * player.xAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);		// dx = vt - 0.5at^2
								player.setXVelocity(xVel);
							}
							if (xVel < 0.1)	{			// if player is still moving when it should be stopped
								player.setXVelocity(0);
								player.standingTexture();
							}
							player.setXPosition(player.getXPosition() + deltaX);
							player.updatePosition(blockScaleFactor);
						}
					} else if (player.getXVelocity() < 0 && !solidOrBreakableBlockToTheLeft()) {	
						// If player is moving left and the block to the left is NOT solid or breakable
						
						if (solidOrBreakableBlockBelow())		// update running texture if player is on the ground
							player.nextTexture(currentFrame);
						
						if (keysPressed[3]) {
							// Left key pressed
							// Accelerate leftward to max speed than travel at that speed
							double xVel = player.getXVelocity();
							double deltaX = 0;
							if (xVel > -player.maxWalkingSpeed) {
								// if below the max speed, increase speed according to equations
								xVel -= player.xAcceleration / GameFrame.framesPerSecond;			// v = at = a/f
								deltaX = xVel / GameFrame.framesPerSecond - 0.5 * player.xAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);		// dx = vt - 0.5at^2
								player.setXVelocity(xVel);
							} else
								// if above max speed, travel at constant speed
								deltaX = xVel / GameFrame.framesPerSecond;			// dx = vt	(when acceleration is 0)
							player.setXPosition(player.getXPosition() + deltaX);
							player.updatePosition(blockScaleFactor);
						} else {
							// Left key released
							// Decelerate back to a stop
							double xVel = player.getXVelocity();
							double deltaX = 0;
							if (xVel < 0) {
								// if moving, start decelerating to a stop
								xVel += player.xAcceleration / GameFrame.framesPerSecond;			// v = at = a/f
								deltaX = xVel / GameFrame.framesPerSecond + 0.5 * player.xAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);		// dx = vt + 0.5at^2
								player.setXVelocity(xVel);
							} 
							if (xVel > -0.1) {				// if player is still moving when it should be stopped
								player.setXVelocity(0);
								player.standingTexture();
							}
							player.setXPosition(player.getXPosition() + deltaX);
							player.updatePosition(blockScaleFactor);
						}
					} else if (player.getXVelocity() == 0 && (!solidOrBreakableBlockToTheRight() || !solidOrBreakableBlockToTheLeft())) {
						// If player is NOT moving and the block to the left or right is NOT solid (0) and NOT breakable (1)
						// Let player start moving for this frame where it is currently not moving
//						player.standingTexture();
						if (keysPressed[1] && !solidOrBreakableBlockToTheRight()) {
							// Right key pressed
							// Accelerate rightwarwd to max speed then travel at that speed
							double xVel = player.getXVelocity();
							double deltaX = 0;
							if ((xVel < player.maxWalkingSpeed)) {
								// if below the max speed, increase speed according to equations
								xVel += player.xAcceleration / GameFrame.framesPerSecond;			// v = at = a/f
								deltaX = xVel / GameFrame.framesPerSecond + 0.5 * player.xAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);		// dx = vt + 0.5at^2
								player.setXVelocity(xVel);
							} else
								// if above max speed, travel at constant speed
								deltaX = xVel / GameFrame.framesPerSecond;			// dx = vt	(when acceleration is 0)
							player.setXPosition(player.getXPosition() + deltaX);
							player.updatePosition(blockScaleFactor);
						} else {
							// Right key released
							// Decelerate back to a stop
							double xVel = player.getXVelocity();
							double deltaX = 0;
							if (xVel > 0) {
								// if moving, start decelerating to a stop
								xVel -= player.xAcceleration / GameFrame.framesPerSecond;			// v = at = a/f
								deltaX = xVel / GameFrame.framesPerSecond - 0.5 * player.xAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);		// dx = vt - 0.5at^2
								player.setXVelocity(xVel);
							} 
							player.setXPosition(player.getXPosition() + deltaX);
							player.updatePosition(blockScaleFactor);
						}
						if (keysPressed[3] && !solidOrBreakableBlockToTheLeft()) {
							// Left key pressed
							// Accelerate leftward to max speed than travel at that speed
							double xVel = player.getXVelocity();
							double deltaX = 0;
							if (xVel > -player.maxWalkingSpeed) {
								// if below the max speed, increase speed according to equations
								xVel -= player.xAcceleration / GameFrame.framesPerSecond;			// v = at = a/f
								deltaX = xVel / GameFrame.framesPerSecond - 0.5 * player.xAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);		// dx = vt - 0.5at^2
								player.setXVelocity(xVel);
							} else
								// if above max speed, travel at constant speed
								deltaX = xVel / GameFrame.framesPerSecond;			// dx = vt	(when acceleration is 0)
							player.setXPosition(player.getXPosition() + deltaX);
							player.updatePosition(blockScaleFactor);
						} else {
							// Left key released
							// Decelerate back to a stop
							double xVel = player.getXVelocity();
							double deltaX = 0;
							if (xVel < 0) {
								// if moving, start decelerating to a stop
								xVel += player.xAcceleration / GameFrame.framesPerSecond;			// v = at = a/f
								deltaX = xVel / GameFrame.framesPerSecond + 0.5 * player.xAcceleration / (GameFrame.framesPerSecond * GameFrame.framesPerSecond);		// dx = vt + 0.5at^2
								player.setXVelocity(xVel);
							} 
							player.setXPosition(player.getXPosition() + deltaX);
							player.updatePosition(blockScaleFactor);
						}
					} else {
						// If the player just hit a wall from the side, than the x velocity is set to 0
						player.setXVelocity(0);
					
						// The player may have overlapped the block slightly if it was moving fast. Puting player exactly where it should be, in the block next to the wall
						if (player.getXPosition() % 1 > 0.5)
							player.setXPosition((int) player.getXPosition() + 1);	// player is overlapped into left wall, place in correct block
						else 
							player.setXPosition((int) player.getXPosition());		// player is slightly right of where it should be (thus, overlapping the right wall), round to correct block
					}	
				
					
				} catch (IndexOutOfBoundsException e) { 
					// Player falls out of the map!
					System.out.println("Player fell out of the map");
//					levelNumberBanner.setLocation((int) (panelPosition + 192), 144);
//					add(levelNumberBanner);
//					update(getGraphics());
					
					// update death counter:
					playerDeathCounter++;
					
					try {
						gameThread.join(1000);				// stop thread for 1 second
					} catch (InterruptedException ee) {}
					
					System.out.println("Deaths: " + playerDeathCounter);
					
					panelPosition = 0;				// reset level panel to starting position (0)
					putPlayerAtStartPosition();
					player.setYVelocity(-2);		// player starts with y velocity of -2
					player.setXVelocity(0);
					gameThread.run();				// restart thread at begininning to reset time trackers
					
				}
				
				// Check if player is in the finish area:
				
				if (levelBoard[(int) player.getXPosition()][(int) player.getYPosition()] != null && levelBoard[(int) player.getXPosition()][(int) player.getYPosition()].getBlockId() == 10) {
					framesPlayerIsInFinishArea++;
					if (framesPlayerIsInFinishArea >= GameFrame.framesPerSecond) {		// been in finish area for longer than 1 second
						System.out.println("Level is finished!");
						System.out.println("It took you " + (playerDeathCounter + 1) + " attempts!");
						try {
							gameThread.join();										// end game thread
						} catch (InterruptedException e) {}
						// Level finished banner (with smooth lowering)
					}
				}
				
				/* [CURRENT] TODO: SEE THE GITHUB
				 * In order of priority:
				 * [x] Improve Player constructor
				 * 	- set start location for player in Level (new instance variable) and use getters and setters to let player know of its position
				 * 	- instantiate instance variables,
				 * 
				 * - Add character running button (shift) [MAYBE WAIT]
				 * 	- Create input and action maps for shift
				 * 	- Add values to keysPressed[] to account for new key
				 * 	- Add if () in the already existing if's that checks if shift is pressed and continues accelerating if it is; decelerates back to max walking speed if not
				 * 
				 * [x] (possible change moving texures) Create character textures
				 * 	- standing, running, and jumping textures
				 * 	- When moving right, player faces right and cycles throught moving textures, changing every (1/2?) seconds
				 * 	- Same for moving left (use a mirror command? or getScaledInstance() with negative inputs?)
				 * 
				 * [x] Implement block properties (and collisions)
				 * 	- ** create super algorithm that controls all player movements, textures, and collisions **
				 * 	- May need to modify current x movements to not occur when colliding with solid block
				 * 	- Use corners of player "block" to see collisions. Collisions with solid block on any side will cause movement to kinda stop. Collision with breakable block
				 * 		on top corders will cause the block to break (become null). Maybe add a breaking animation block
				 * 	- Use sheet to see block properties (blockId % 6)
				 * 	- Player moving textures change depending on players speed
				 * 
				 * [x] Add y velocity calculater
				 * 	- Constant downwards acceleration (freefall)
				 * 	- What is initial y velocity when a jump happens that causes player to jump 3.5 blocks high (calculate this)
				 * 	- Derive some physics equation
				 * 
				 * - Extend finish area to take up the whole window (24 blocks)
				 * 
				 * - Level completion and statistics
				 * 	- Timer to time how long the level took
				 * 	- Define a player death
				 * 	- Death counter
				 * 
				 * [x] Fix hyperlink comments in GameFrame!!
				 * 	- use {@link} instead
				 * 
				 * Bugs:
				 */
				
			}
			
			if (System.nanoTime() >= oneSecondStart + 1000000000) {		// Executes every second
				
				// Update the one second tracker:
				oneSecondStart = System.nanoTime();
				
				// Print out and reset statistics:
				System.out.println("Frames: " + frameCount);
				System.out.println("Executions: " + executionCount);
				frameCount = 0;
				executionCount = 0;
				currentFrame = 0;
				
				if (!gameThread.isAlive()) {		// if thread died
					System.out.println("The thread is dead!");
				}
				
				
			}
			
		}
		
	}
	
	/**
	 * Sets this level board to the board that is inputed. Note {@code board} should be constructed in the 
	 * {@code GameFrame} class.
	 * @throws IllegalArgumentException if {@code board} doesnt have the same dimensions as this level board
	 * @param board - the level board
	 */
	public void setLevelBoard (Block[][] board) {
		if (board.length != levelBoard.length || board[0].length != levelBoard[0].length)
			throw new IllegalArgumentException("The board that was created doesnt match the dimensions of the level board!");
		else
			levelBoard = board;
	}
	
	/**
	 * Places every block in {@code levelBoard} onto this panel, excluding null Blocks
	 */
	public void putBlocksOnPanel () {
		for (int r = 0; r < levelBoard.length; r++) {
			for (int c = 0; c < levelBoard[0].length; c++) {
				if (levelBoard[r][c] != null) {
					add(levelBoard[r][c]);
					levelBoard[r][c].setLocation(r * Block.defaultBlockResolution * blockScaleFactor, c * Block.defaultBlockResolution * blockScaleFactor);
				}
			}
		}
	}

	private static final long serialVersionUID = 1L;
	
}
