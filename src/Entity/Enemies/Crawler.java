package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import TileMap.TileMap;
import Entity.Animation;
import Entity.Enemy;

public class Crawler extends Enemy {
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {7,7};
	
	private static final int DYING = 0;
	private static final int CRAWLING = 1;

	public Crawler(TileMap tm) {
		super(tm);
		
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
	
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
			
		// check dying
		if(dead && currentAction != DYING) {
			dx = 0;
			animation.setFrames(sprites.get(DYING));
			animation.setDelay(100);
			currentAction = DYING;
			width = 20;
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
		} else {
			moveSpeed = 0.3;
			fallSpeed = 0.2;
			maxFallSpeed = 10;
		}
			
		// update animation
		animation.update();
	}
	
public void draw(Graphics2D g) {
		
		setMapPosition();
		
		super.draw(g);
		
	}

}
