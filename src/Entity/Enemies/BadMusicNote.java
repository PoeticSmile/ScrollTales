package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import Entity.Animation;
import Entity.MapObject;
import TileMap.TileMap;

public class BadMusicNote extends MapObject {

	public boolean hit;
	private int damage;
	private double ds;
	private boolean remove;
	private BufferedImage[] sprites;
	private BufferedImage[] hitSprites;
	private boolean spawnProtection;
	private long spawnProtectionTimer = 0;
	
	public BadMusicNote(TileMap tm, boolean fl, boolean higher, double playerx, double playery, double x, double y) {
		
		super(tm);
		
		facingLeft = !fl;
		if(facingLeft) {
			if(higher)	ds = Math.sqrt((x-playerx)*(x-playerx) + (playery-y)*(playery-y));
			else		ds = Math.sqrt((x-playerx)*(x-playerx) + (y-playery)*(y-playery));
		} else{
			if(higher)	ds = Math.sqrt((playerx-x)*(playerx-x) + (playery-y)*(playery-y));
			else		ds = Math.sqrt((playerx-x)*(playerx-x) + (y-playery)*(y-playery));
		}
		if(facingLeft) { 
			dx = -(x-playerx)/ds;
			if(x-playerx == 0)	moveSpeed = 0; 
			else moveSpeed = -5;
		}
		else {
			dx = (playerx - x)/ds;
			if(playerx-x == 0) moveSpeed = 0;
			else moveSpeed = 5;
		}
		if(higher) {
			dy = (playery-y)/ds;
			if(playery-y == 0) fallSpeed = 0;
			else fallSpeed = 5;
		}
		else {
			dy = -(y-playery)/ds;
			if(y-playery == 0) fallSpeed = 0;
			else fallSpeed = -5;
		}
	
		
		width = 8;
		height = 10;
		cwidth = 8;
		cheight = 10;
		damage = 1;
		
		// load sprites
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites.Enemies/BadMusicNoteSprites.gif"));
			
			sprites = new BufferedImage[2];
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
		
		spawnProtection = true;
		spawnProtectionTimer = System.nanoTime();
	}
	
	public void setHit() {
		if(hit) return;
		hit = true;
		animation.setFrames(hitSprites);
		animation.setDelay(70);
		dx = 0;
		dy = 0;
	}
	
	public boolean shouldRemove() { return remove; }
	public int getDamage() { return damage; }
	
	public void update() {
		
		if(spawnProtection) {
			moveWithoutCollisionDetection();
		} else checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(dx == 0 && dy == 0 && !hit) setHit();
		animation.update();
		if(hit && animation.hasPlayedOnce()) {
			remove = true;
		}
		
		if(spawnProtection) {
			long elapsed = (System.nanoTime() - spawnProtectionTimer) / 1000000;
			if(elapsed > 1000) {
				spawnProtectionTimer = 0;
				spawnProtection = false;
			}
		}
				
	}
	
	public void draw(Graphics2D g) {
		
		setMapPosition();
		
		super.draw(g);
	}
	
	
}
