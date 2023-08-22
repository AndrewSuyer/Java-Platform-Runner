package platformRunner;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * A list of Blocks is used to make up levels for the game. Every type of block has a unique integer
 * {@code blockId} which is used to catagorize its' properties and texture. There are 6 types of blocks:
 * Solid, Breakable, Transparent, Deadly, Background, and other. The remainder when dividing {@code blockId}
 * by 6 is used to determine the block property. Ex: if {@code blockId} is 13, than the block is breakable
 * because 13 % 6 = 1. 
 * 
 * @author Andrew Suyer
 */
public class Block extends JLabel {
	
	/** The default resolution of a block before scaling */
	public static final int defaultBlockResolution = 16;
	
	/** The Id cooresponding to the type block this is, see Id Doc for all block Id's */
	private int blockId;
	
	public int getBlockId () { return blockId; }
	
	/** 16x16 pixel icon for this Block texture */
	private ImageIcon texture;
	
	/**
	 * Creates a Block. The {@code blockId} specifies what kind of block this is, and it's used to determine
	 * block texture as well as block properties. The {@code scale} is used to scale the texture of the block
	 * when it's displayed on the panel
	 * @param Id - Block Id
	 * @param scale - Block scale factor
	 */
	public Block (int Id, int scale) {
		
		// Initializing instance variables and textures:
		blockId = Id;
		String imgFilepath = blockIdToTexture(Id);
		texture = new ImageIcon(imgFilepath);
		texture = new ImageIcon(texture.getImage().getScaledInstance(defaultBlockResolution * scale, defaultBlockResolution * scale, Image.SCALE_SMOOTH));		// scaling texture
		
		// Label setup:
		setIcon(texture);
		setSize(defaultBlockResolution * scale, defaultBlockResolution * scale);
		setVisible(true);
	}
	
	/**
	 * Returns the filepath for the texture based on the {@code blockId}
	 * @param id - blockId for the desired texture
	 * @return String to the filepath of the texture
	 */
	private String blockIdToTexture (int id) {
		switch(id) {
		case 0:
			// Dirt
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\0_Dirt.png";
		case 1:
			// Wood
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\1_Wood.png";
		case 2:
			// Cloud
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\2_Cloud.png";
		case 3:
			// Spike
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\3_Spike.png";
		case 4:
			// Gray background
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\4_GrayBackground.png";
		case 6:
			// Grass
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\6_Grass.png";
		case 9:
			// Lava
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\9_Lava.png";
		case 10:
			// Finish area gold
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\10_FinishAreaGold.png";
		case 12:
			// Rock
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\12_Rock.png";
		case 15:
			// Spike with gray background
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\15_Spike_WithGrayBackground.png";
		case 16:
			// Cyan background
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\16_CyanBackground.png";
		case 18:
			// Brick
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\18_Brick.png";
		case 21:
			// Spike with cyan background
			return "D:\\\\Gimp\\\\PlatformRunnerTextures\\\\21_Spike_WithCyanBackground.png";
		default:
			// Super mario bros tiger block
			return "D:\\\\Gimp\\\\SuperMarioBrosTigerBlock.png";
		}
	}
	
	private static final long serialVersionUID = 1L;

}
