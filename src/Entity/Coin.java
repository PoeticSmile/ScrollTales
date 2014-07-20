package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import TileMap.TileMap;

public class Coin extends MapObject{
	
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {5, 5};
	
	private static final int TURNING = 0;
	
	private boolean remove;
	
	public Coin(TileMap tm) {
		super(tm);
		
		width = 15;
		height = 15;
		cwidth = 15;
		cheight = 15;
		
		moveSpeed = maxSpeed = stopSpeed = fallSpeed = maxFallSpeed= jumpStart = stopJumpSpeed = 0;
		
		
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites.Player/Coin.gif"));
			sprites = new ArrayList<BufferedImage[]>();
			
			for(int i = 0; i < 2; i++) {
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				
				for(int j = 0; j < numFrames[i]; j++) {
					
					bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
					
				}
				sprites.add(bi);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = TURNING;
		animation.setFrames(sprites.get(TURNING));
		animation.setDelay(130);
		
		
	}
	
	public void setAction(int a) {
		if(currentAction != a) {
			currentAction = a;
			animation.setFrames(sprites.get(a));
		}
	}
	
	public void setDelay(int d) {
		animation.setDelay(d);
	}
	
	public int getDelay() { return animation.getDelay(); }
	
	public int getNumPlays() { return animation.getNumPlays(); }
	
	public boolean shouldRemove() { return remove; }
	
	
	public void update() {
		animation.update();
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();
		super.draw(g);
		
	}

}
