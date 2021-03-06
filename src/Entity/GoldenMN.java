package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import Audio.AudioPlayer;
import Entity.Enemies.BadMusicNote;
import Main.GamePanel;
import TileMap.TileMap;

public class GoldenMN extends MapObject {
	
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {10, 12, 5, 1};
	
	private final int CHILLIN = 0;
	private final int CHARGING = 1;
	private final int DYING = 2;
	private final int DEAD = 3;
	
	private int health;
	
	private int collisionDamage;
	private int missileDamage;
	private ArrayList<BadMusicNote> badMusicNotes;
	private int playerx, playery;
	
	private boolean flinching;
	private long flinchTimer;
	private int numPlaysBeforeDying = 0;
	
	private boolean dead;
	private boolean canSpawnHeart;
	
	private HashMap<String, AudioPlayer> sfx;
	
	public GoldenMN() {
		
		width = 20;
		height = 20;
		cwidth = 28;
		cheight = 20;
		
		moveSpeed = maxSpeed = stopSpeed = fallSpeed = maxFallSpeed= jumpStart = stopJumpSpeed = 0;
		
		health = 2;
		
		collisionDamage = 1;
		missileDamage = 1;
		badMusicNotes = new ArrayList<BadMusicNote>();
		
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites.Player/GoldenMN.gif"));
			sprites = new ArrayList<BufferedImage[]>();
			
			for(int i = 0; i < numFrames.length; i++) {
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				
				for(int j = 0; j < numFrames[i]; j++) {
					
					bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
					
				}
				sprites.add(bi);
			}
			sfx = new HashMap<String, AudioPlayer>();
			sfx.put("hitted", new AudioPlayer("/SFX/enemyHitOne.wav"));
			sfx.put("dying", new AudioPlayer("/SFX/dyingEnemyOne.wav"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = CHILLIN;
		animation.setFrames(sprites.get(CHILLIN));
		animation.setDelay(120);
		
		
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
	
	public void hit(int damage) {
		if(flinching) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) {
			if (!dead) {
				dead = true;
				numPlaysBeforeDying = animation.getNumPlays();
				sfx.get("dying").play();
			}
			return;
		} else {
			flinching = true;
			flinchTimer = System.nanoTime();
		}
		sfx.get("hitted").play();
	}
	
	public void charge() {
		if(!dead && !flinching && currentAction == CHILLIN) {
			animation.setFrames(sprites.get(CHARGING));
			animation.setDelay(80);
			currentAction = CHARGING;
		}
	}
	
	public void spawnBadMusicNotes() {
		boolean fl, h;
		if(x > playerx) fl = true;
		else fl = false;
		if(y > playery) h = true;
		else h = false;
		BadMusicNote ms = new BadMusicNote(fl, h, playerx, playery, x, y);
		ms.setTileMap(getTileMap());
		ms.setPosition(x, y);
		badMusicNotes.add(ms);
		animation.setFrames(sprites.get(CHILLIN));
		animation.setDelay(120);
		currentAction = CHILLIN;
	}
	
	
	public int getDelay() { return animation.getDelay(); }
	public int getCurrentAction() { return currentAction; }
	public int getNumPlays() { return animation.getNumPlays(); }
	public int getCollisionDamage() { return collisionDamage; }
	public int getMissileDamage() { return missileDamage; }
	public ArrayList<BadMusicNote> getBadMusicNotes() { return badMusicNotes; }
	public void clearBadMusicNotes() {
		for (int i = 0; i < badMusicNotes.size(); i++) {
			badMusicNotes.remove(i);
		}
	}
	public void setPlayerPosition(int playerx, int playery) {
		this.playerx = playerx;
		this.playery = playery;
	}
	public boolean isFlinching() { return flinching; }
	public boolean isDead() { return dead; }
	public boolean canSpawnHeart() { return canSpawnHeart; }
	
	public void cantSpawnHeart() {
		canSpawnHeart = false;
	}
	
	
	public void update() {

		// check done flinching
		if(flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 750) {
				flinching = false;
			}
		}
		
		if(currentAction == CHARGING && animation.hasPlayedOnce()) {
			setPlayerPosition(tileMap.getPlayerX(), tileMap.getPlayerY());
			spawnBadMusicNotes();
		}
		
		if(!dead && currentAction == CHILLIN && animation.getDelay() != 120) {
			animation.setDelay(120);
		}
		
		if(dead) {
			if(currentAction == CHILLIN && animation.getDelay() != 10) {
				animation.setDelay(10);
			}
			if(currentAction == CHILLIN && numPlaysBeforeDying + 3 < animation.getNumPlays()) {
				animation.setFrames(sprites.get(DYING));
				currentAction = DYING;
				animation.setDelay(145);
			}
			
		}
		
		if(currentAction == DYING && animation.hasPlayedOnce()) {
			canSpawnHeart = true;
			currentAction = DEAD;
			animation.setFrames(sprites.get(DEAD));
			animation.setDelay(999);
			animation.setUpdateAnimation(false);
		}
		
		// update BadMusicNotes
		for(int i = 0; i < badMusicNotes.size(); i++) {
			BadMusicNote b = badMusicNotes.get(i);
			b.update();
			if(b.shouldRemove() ||
					b.x + xmap + b.width < 0 ||
					b.x + xmap - b.width > GamePanel.WIDTH ||
					b.y + ymap + b.height < 0 ||
					b.y + ymap - b.height > GamePanel.HEIGHT
				) badMusicNotes.remove(i);
		}
		
		animation.update();
		
		
		
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();
		
		// draw flinching
		if(flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) {
				return;
			}
		}
		
		// draw BadMusicNotes
		for(int i = 0; i < badMusicNotes.size(); i++) {
			badMusicNotes.get(i).draw(g);
		}
		
		super.draw(g);
	}

}
