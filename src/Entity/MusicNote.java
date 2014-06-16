package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import TileMap.TileMap;

public class MusicNote extends MapObject {

	public boolean hit;
	private boolean remove;
	private BufferedImage[] sprites;
	private BufferedImage[] hitSprites;
	
	public MusicNote(TileMap tm, boolean right) {
		
		super(tm);
		
		facingLeft = right;
		
		moveSpeed = 4;
		if(facingLeft) dx = moveSpeed;
		else dx = -moveSpeed;
		
		width = 8;
		height = 10;
		cwidth = 8;
		cheight = 10;
		
		// load sprites
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites.Player/MusicNoteSprites.gif"));
			
			sprites = new BufferedImage[4];
			for(int i = 0; i < sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
			}
			
			hitSprites = new BufferedImage[4];
			for(int i = 0; i < hitSprites.length; i++) {
				hitSprites[i] = spritesheet.getSubimage(i * width, height, width, height);
			}
			
			animation = new Animation();
			animation.setFrames(sprites);
			animation.setDelay(70);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setHit() {
		if(hit) return;
		hit = true;
		animation.setFrames(hitSprites);
		animation.setDelay(50);
		dx = 0;
	}
	
	public boolean shouldRemove() { return remove; }
	public boolean isHitted() { return hit; }
	
	public void update() {
		
		
		//moveWithoutCollisionDetection();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(dx == 0 && !hit) setHit();
		
		animation.update();
		if(hit && animation.hasPlayedOnce()) {
			remove = true;
		}
		
	}
	
	public void draw(Graphics2D g) {
		
		setMapPosition();
		super.draw(g);
	}
	
	
}
