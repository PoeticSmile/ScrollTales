package Entity;

import Audio.AudioPlayer;
import TileMap.TileMap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.imageio.ImageIO;



public class Love extends MapObject {
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {1, 5};
	
	private static final int CHILLIN = 0;
	private static final int SHINING = 1;
	
	private boolean hitted;
	private boolean isCollected;
	private boolean remove;
	private double smallJumpStart;
	
	private Hashtable<String, AudioPlayer> sfx;

	public Love() {
		
		width = 20;
		cwidth = 20;
		height = 20;
		cheight = 20;
		
		moveSpeed = maxSpeed = 3.32;
		stopSpeed = 0.267;
		jumpStart = -2.57;
		smallJumpStart = -1.47;
		stopJumpSpeed = 0.2;
		fallSpeed = 0.27;
		maxFallSpeed = 6;
		
		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites.Player/Love.gif"));
			sprites = new ArrayList<BufferedImage[]>();
			
			for(int i = 0; i < 2; i++) {
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++) {
					bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
				}
				sprites.add(bi);
			}
			sfx = new Hashtable<String, AudioPlayer>();
			sfx.put("spawned", new AudioPlayer("/SFX/HeartSpawned.wav"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = CHILLIN;
		animation.setFrames(sprites.get(CHILLIN));
		animation.setDelay(3000);

		
	}
	
	private void getNextPosition() {
		
		calculateCorners(x, y);
		
		// falling
		if(falling) {
			if(!jumping) {
				dy += fallSpeed;
				if(dy > maxFallSpeed) dy = maxFallSpeed;
			}
			else if(dy < 0) dy += stopJumpSpeed;
			if(dy > 0) jumping = false;		
							
		}
		
		
		
	/*	if(tileMap.getType(currRow, currCol-1) == 1) {
			falling = true;
		} else if(tileMap.getType(currRow, currCol+1) == 1) {
			jumping = true;
			falling = true;
			dy = jumpStart;
		}*/
		
	//	System.out.println(currRow);
	/*	System.out.println("topleft: " + getTopLeft());
		System.out.println("BottomLeft: " + getBottomLeft());
		System.out.println("topRight: " + getTopRight());
		System.out.println("bottomRight: " + getBottomRight());*/
		
		
		if(dx > 0) {
			dx -= stopSpeed;
			if(dx < 0) dx = 0;
		}
		if(dx < 0) {
			dx += stopSpeed;
			if(dx > 0) dx = 0;
		}
		
		
		
		
	}
	
	public void initSpawning() {
		sfx.get("spawned").play();
		// row = y; col = x
		calculateCorners(x, y);
		int cr = (int) y/tileSize;
		int cc = (int) x/tileSize;
		if(tileMap.getType(cr+1, cc) == 0) {
			// if underneath is reachable fall
			if(tileMap.getType(cr, cc-1) == 0 &&
					tileMap.getType(cr+1, cc-1) == 0 ||
					tileMap.getType(cr, cc+1) == 0 &&
					tileMap.getType(cr+1, cc+1) == 0) {
					y++;
					falling = true;
			}
		} else if(tileMap.getType(cr-1, cc) == 0) {
			// if upon is reachable jump
			if(tileMap.getType(cr-1, cc) == 0 && 
					tileMap.getType(cr-1, cc-1) == 0 || tileMap.getType(cr-1, cc) == 0 &&
					tileMap.getType(cr-1, cc+1) == 0) {
						jumping = true;
						falling = true;
						dy = jumpStart;
						y--;
			}
		}
		if(!jumping && !falling) {
			if(tileMap.getType(cr, cc-1) == 0)  {
				dx = -moveSpeed;
				x--;
			} else {
				dx = moveSpeed;
				x++;
			}
		}

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
	
	public void isHit(boolean hit) {
		hitted = hit;
	}
	
	public void collected(boolean c) {
		isCollected = c;
	}
	public boolean isCollected() { return isCollected; }
	
	public int getDelay() { return animation.getDelay(); }
	public int getNumPlays() { return animation.getNumPlays(); }
	public boolean shouldRemove() { return remove; }
	
	public void update() {
		getNextPosition();
		
		if(falling && (getTopLeft() == 1 || getTopRight() == 1) || jumping && (getBottomLeft() == 1 || getBottomRight() == 1) ||
			dx > 0 && (getBottomLeft() == 1 || getBottomRight() == 1) || dx < 0 && (getBottomRight() == 1 || getBottomLeft() == 1)) {
			
			moveWithoutCollisionDetection();
			
		} else {
			checkTileMapCollision();
		}
		setPosition(xtemp, ytemp);
		
		if(currentAction == CHILLIN && animation.hasPlayedOnce() && !isCollected) {
			currentAction = SHINING;
			animation.setFrames(sprites.get(SHINING));
			animation.setDelay(60);
		}
		
		if(currentAction == SHINING && animation.hasPlayedOnce()) {
			currentAction = CHILLIN;
			animation.setFrames(sprites.get(CHILLIN));
			animation.setDelay(3000);
		}
		
		if(hitted) {
			dy = smallJumpStart;
			falling = true;
			hitted = false;
		}
		
		if(isCollected) {
			width += 17;
			height += 17;
			dx = dy = 0;
			falling = jumping = false;
			if(width < 21) {
				animation.setFrames(sprites.get(CHILLIN));
				currentAction = CHILLIN;
				animation.setDelay(0);
			}
			
		}
		
		if(width > 1700) remove = true;
		
		animation.update();
		 
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();
		super.draw(g);
	}

}
