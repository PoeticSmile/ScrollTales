package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import TileMap.TileMap;
import Audio.AudioPlayer;
import Entity.Animation;
import Entity.Enemy;

public class Crawler extends Enemy {
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {4,4};
	
	private static final int CRAWLING = 0;
	private static final int DYING = 1;
	
	private HashMap<String, AudioPlayer> sfx;

	public Crawler() {
		
		moveSpeed = 0.3;
		maxSpeed = 0.3;
		fallSpeed = 0.2;
		maxFallSpeed = 10;
		
		width = 20;
		height = 20;
		cwidth = 20;
		cheight = 20;
		
		health = maxHealth = 1;
		damage = 1;
		
		// load Sprites
		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites.Enemies/Crawler.gif"));
			sprites = new ArrayList<BufferedImage[]>();
			
			for(int i = 0; i < 2; i++) {
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				
				for(int j = 0; j < numFrames[i]; j++) {
					bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
				}
				
				sprites.add(bi);
			}
			sfx = new HashMap<String, AudioPlayer>();
			sfx.put("hit", new AudioPlayer("/SFX/crawlerHit.wav"));
			sfx.put("spawnedHeart", new AudioPlayer("/SFX/HeartSpawned.wav"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites.get(CRAWLING));
		animation.setDelay(300);
		currentAction = CRAWLING;
		left = true;
		facingLeft = true;
		
		
	}
	
	public void hit(int damage) {
		sfx.get("hit").play();
		super.hit(damage);
	}
	
	private void getNextPosition() {
		
		// movement
		if(left) {
		dx -= moveSpeed;
			if(dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		}
		else if(right) {
			dx += moveSpeed;
			if(dx > maxSpeed) {
				dx = maxSpeed;
			}
		}
		
		// falling
		if(falling) {
			dy += fallSpeed;
		}
		
		
	}
	
	public void update() {
	if(!notOnScreen()) {
		// update position
		getNextPosition();
		if(dead) moveWithoutCollisionDetection();
		else checkTileMapCollision();
		setPosition(xtemp, ytemp);
			
		// check dying
		if(dead && currentAction != DYING) {
			dx = 0;
			animation.setFrames(sprites.get(DYING));
			animation.setDelay(200);
			currentAction = DYING;
			width = 20;
			moveSpeed = 0;
		}
		
		if (dead && animation.hasPlayedOnce() && animation.getDelay() != 9999) {
			animation.setDelay(9999);
			animation.setFrame(3);
			dy = -17;
			sfx.get("spawnedHeart").play();
		}
		
		//if it hits a wall, go other direction
		if((right && dx == 0) && !dead) {
			right = false;
			left = true;
			facingLeft = true;
		}
		else if((left && dx == 0) && !dead) {
			right = true;
			left = false;
			facingLeft = false;
		}
		
		if(notOnScreen()) {
			moveSpeed = 0;
			fallSpeed = 0;
			maxFallSpeed = 0;
		} else if (!dead) {
			moveSpeed = 0.3;
			fallSpeed = 0.2;
			maxFallSpeed = 10;
		}
			
		// update animation
		animation.update();
	} else if (dead) remove = true;
	}
	
public void draw(Graphics2D g) {
		setMapPosition();
		
		super.draw(g);
		
	}

}
