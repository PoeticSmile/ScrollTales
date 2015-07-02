package Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import Main.GamePanel;
import TileMap.*;
import Audio.AudioPlayer;
import Entity.Enemies.BadMusicNote;
import Entity.MapComponents.Coin;
import Entity.MapComponents.Door;
import Entity.MapComponents.Sign;

public class Player extends MapObject {
	
	
	// player stuff
	private int health;
	private int maxHealth;
	private int fire;
	private int maxFire;
	private boolean dead;
	private boolean flinching;
	private long flinchTimer;
	private boolean isOpeningDoor;
	
	
	// attacking stuff
	private double mx, my;
	private boolean firing;
	public boolean jumpKeyPressed;
	private boolean jumpAttack;
	private int jumpAttackDamage;
	private int musicNoteDamage;
	private int musicNoteDamageCharged;
	private ArrayList<MusicNote> musicNotes;
	private ArrayList<ChargedMissile> chargedMissiles;
	private boolean charging;
	private boolean charged;
	private long chargeTimer;
	
	private int numHearts;
	private int numEnemies;
	private int numCoins;
	private int numGMNBoxes;
	
	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {10,10,10,10,6,10};
	
	// animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int CHARGING = 4;
	private static final int DYING = 5;
	
	private HashMap<String, AudioPlayer> sfx;
	
	
	public Player() {
		
		width = 20;
		height = 20;
		cwidth = 20;
		cheight = 20;
		
		moveSpeed = 0.7;
		maxSpeed = 2.7;
		stopSpeed = 0.3;
		fallSpeed = 0.17;
		maxFallSpeed = 6;
		jumpStart = -6;
		stopJumpSpeed = 0.2;
		
		facingLeft = true;
		
		health = maxHealth = 2;
		maxFire = 3;
		
		jumpAttackDamage = 1;
		musicNoteDamage = 1;
		musicNoteDamageCharged = 2;
		musicNotes = new ArrayList<MusicNote>();
		chargedMissiles = new ArrayList<ChargedMissile>();
		
		// load sprites
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites.Player/playersprites.gif"));
			sprites = new ArrayList<BufferedImage[]>();
			
			for(int i = 0; i < numFrames.length; i++) {
				
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				
				for(int j = 0; j < numFrames[i]; j++) {
					
					bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
				}
				
				sprites.add(bi);
			}
		
			sfx = new HashMap<String, AudioPlayer>();
			sfx.put("jump", new AudioPlayer("/SFX/jumpLeise.wav"));
			sfx.put("coin", new AudioPlayer("/SFX/coin.wav"));
			sfx.put("missile", new AudioPlayer("/SFX/missile.wav"));
			sfx.put("hitted", new AudioPlayer("/SFX/playerHitted.wav"));
			sfx.put("coinsCollected", new AudioPlayer("/SFX/coinsCollected.wav"));
			sfx.put("pickedUpHeart", new AudioPlayer("/SFX/pickedUpHeart.wav"));
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(700);
		
		
		
		
	}
	
	public int getHealth() { return health; }
	public int getMaxhealth() { return maxHealth; }
	public int getFire() { return fire; }
	public int getMaxFire() { return maxFire; }
	public int getNumEnemies() { return numEnemies; }
	public int getNumCoins() { return numCoins; }
	public int getNumHearts() { return numHearts; }
	
	public void setNumEnemies(int num) { numEnemies = num; }
	public void setDoorOpening(boolean b) { isOpeningDoor = b; }
	public boolean isOpeningDoor() { return isOpeningDoor; }
	
	public void clearMusicNotes() {
		for (int i = 0; i < musicNotes.size(); i++) {
			musicNotes.remove(i);
		}
	}
	public void setFiring() {
		if(fire < maxFire  && !firing) firing = true;
	}
	
	public void checkLove(ArrayList<Love> hearts) {
		for(int i = 0; i < hearts.size(); i++) {
			Love heart = hearts.get(i);
			
			if(this.intersects(heart) && !heart.isCollected()) {
				heart.collected(true);
				sfx.get("pickedUpHeart").play();
			}
			if(heart.shouldRemove()) {
				hearts.remove(heart);
				numHearts++;
			}
			
			for(int j = 0; j < musicNotes.size(); j++) {
				if(musicNotes.get(j).intersects(heart)) heart.isHit(true);
			}
			for(int j = 0; j < chargedMissiles.size(); j++) {
				if (chargedMissiles.get(j).intersects(heart)) heart.isHit(true);
			}
		}
	}
	
	public void checkLevelEnd(LevelEndLove leh) {
		if(intersects(leh)) {
			leh.collected();
		}
	}
	public void checkAttack(ArrayList<Enemy> enemies) {
		
		// loop through enemies
		for(int i = 0; i < enemies.size(); i++) {
			
			Enemy e = enemies.get(i);
			
			// check musicNote attack
			for(int j = 0; j < musicNotes.size(); j++) {
				if(musicNotes.get(j).intersects(e) && !musicNotes.get(j).isHitted() && !e.dead) {
					e.hit(musicNoteDamage);
					musicNotes.get(j).setHit();
					continue;
				}
			}
			// Check chargedMissile attack
			for (int j = 0; j < chargedMissiles.size(); j++) {
				if (chargedMissiles.get(j).intersects(e) && !chargedMissiles.get(j).isHitted() && !e.dead) {
					e.hit(musicNoteDamageCharged);
					chargedMissiles.get(j).setHit();
					continue;
				}
			}
			
			// check enemy collison
			if(intersects(e) && (!e.dead || !e.flinching)) {
				hit(e.getDamage());
			}
		}
		
		
	}
	
	public void checkBadMusicNotes(ArrayList<BadMusicNote> bmn) {
		
		for(int i = 0; i < bmn.size(); i++) {
			BadMusicNote b = bmn.get(i);
			if(intersects(b)) {
				hit(b.getDamage());
				b.setHit();
			}
			
			for(int j = 0; j < musicNotes.size(); j++) {
				if(musicNotes.get(j).intersects(b) && !musicNotes.get(j).isHitted()) {
					b.setHit();
					musicNotes.get(j).setHit();
				}
			}
			for (int j = 0; j < chargedMissiles.size(); j++) {
				if (chargedMissiles.get(j).intersects(b) && !chargedMissiles.get(j).isHitted()) {
					b.setHit();
					chargedMissiles.get(j).setHit();
				}
			}
			
		}
	}
	
	public void checkCoin(ArrayList<Coin> coins) {
		
		for(int i = 0; i < coins.size(); i++) {
			Coin c = coins.get(i);
			
			if(c.getCurrentAction() == 1 && c.animation.hasPlayedOnce()) {
				coins.remove(i);
				i++;
			}
			
			if(intersects(c) && c.getCurrentAction() != 1) {
				sfx.get("coin").play();
				c.setAction(1);
				c.setDelay(100);
			}
		
			// check musicnote collision
			for(int j = 0; j < musicNotes.size(); j++) {
				MusicNote ms = musicNotes.get(j);
				if(c.intersects(ms)) {
					c.setDelay(20);
				} 
			}
			for(int j = 0; j < chargedMissiles.size(); j++) {
				if(chargedMissiles.get(j).intersects(c)) {
					c.setDelay(20);
				} 
			}
			
			if(c.getDelay() == 20 && c.getNumPlays() > 3) {
					c.setDelay(130);
			}
			
			if(coins.size() == 0) sfx.get("coinsCollected").play();
		}
		
		numCoins = coins.size();
		
	}
	
	public void checkGoldenMN(ArrayList<GoldenMN> gmnBoxes) {
		
		for(int i = 0; i < gmnBoxes.size(); i++) {
			GoldenMN b = gmnBoxes.get(i);
			
			// check musicNote attack
			for(int j = 0; j < musicNotes.size(); j++) {
				if(musicNotes.get(j).intersects(b) && !musicNotes.get(j).isHitted() && b.getCurrentAction() == 0) {
					b.hit(musicNoteDamage);
					musicNotes.get(j).setHit();
					continue;
				}
			}
			// check chargedMissile attack
			for (int j = 0; j < chargedMissiles.size(); j++) {
				if (chargedMissiles.get(j).intersects(b) && !chargedMissiles.get(j).isHitted()) {
					b.hit(musicNoteDamageCharged);
					chargedMissiles.get(j).setHit();
					continue;
				}
			}
			
		}
		
		numGMNBoxes = gmnBoxes.size();
	}
	
	public void checkMusicBox(ArrayList<MusicBox> musicBoxes) {
		
		for (int i = 0; i < musicBoxes.size(); i++) {
			MusicBox m = musicBoxes.get(i);
			
			if (m.getCurrentAction() == 1) {
				// check musicNote attack
				for (int j = 0; j < musicNotes.size(); j++) {
					if (musicNotes.get(j).intersects(m) && !musicNotes.get(j).isHitted()) {
						// hit musicBox
						m.wasActivated();
						musicNotes.get(j).setHit();
						continue;
					}
				}
				for (int j = 0; j < chargedMissiles.size(); j++) {
					if (chargedMissiles.get(j).intersects(m) && !chargedMissiles.get(j).isHitted()) {
						m.wasActivated();
						chargedMissiles.get(j).setHit();
						continue;
					}
				}
			}
		}
	}
	
	public void checkSigns(ArrayList<Sign> signs) {
		for (int i = 0; i < signs.size(); i++) {
			if (this.intersects(signs.get(i))) {
				signs.get(i).setEnterButtonVisibility(true);
			} else signs.get(i).setEnterButtonVisibility(false);
		}
	}
	
	public void checkDoors(ArrayList<Door> doors) {
		for (int i = 0; i < doors.size(); i++) {
			Door d = doors.get(i);
			if (this.intersects(d) && !isOpeningDoor) {
				d.setOpenDoorButtonVisibility(true);
			} else d.setOpenDoorButtonVisibility(false);
		}
	}
	
	public void checkHeartCageAttack(HeartCage hc) {
		for(int i = 0; i < musicNotes.size(); i++) {
			MusicNote ms = musicNotes.get(i);
			if(!hc.isDead() && ms.intersects(hc) && !ms.isHitted()) {
				hc.hit(musicNoteDamage);
				ms.setHit();
				continue;
			}
		}
		for (int i = 0; i < chargedMissiles.size(); i++) {
			ChargedMissile cm = chargedMissiles.get(i);
			if (!hc.isDead() && cm.intersects(hc) && !cm.isHitted()) {
				hc.hit(musicNoteDamageCharged);
				cm.setHit();
				continue;
			}
		}
	}
	public void hit(int damage) {
		if(flinching || isOpeningDoor) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) dead = true;
		if (!isDead()) {
			sfx.get("hitted").play();
			flinching = true;
			flinchTimer = System.nanoTime();
		}
		
	}
	
	public void charge() {
		if (charged) return;
		charging = true;
		chargeTimer = System.nanoTime();
		currentAction = CHARGING;
		animation.setFrames(sprites.get(CHARGING));
		animation.setDelay(500);
	}
	public void chargeEnded() {
		charging = false;
		if (!charged) {
			currentAction = IDLE;
			animation.setFrames(sprites.get(IDLE));
			animation.setDelay(700);
		}
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
		else {
			if(dx > 0) {
				dx -= stopSpeed;
				if(dx < 0) {
					dx = 0;
				}
			}
			else if(dx < 0) {
				dx += stopSpeed;
				if(dx > 0) {
					dx = 0;
				}
			}
		}
		
		if(left && right) {
			dx = 0;
		}
		
		
		
		// jumping
		if(jumping && !falling) {
			sfx.get("jump").play();
			dy = jumpStart;
			falling = true;
		}
		
		// falling
		if(falling && !jumpAttack) {
			
			dy += fallSpeed;
			
			if(dy > 0) jumping = false;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;
			
			if(dy > maxFallSpeed) dy = maxFallSpeed;
		}
		
		// jumpAttack
		if(jumpAttack) {
			if(jumpKeyPressed) dy = -6;
			else dy = -3;
			falling = true;
			jumpAttack = false;
			jumpKeyPressed = false;
		}
		
		if(dy < 0) {
			jumpKeyPressed = false;
		}
		
	}

	public void update() {
		
	// update position
	getNextPosition();
	checkTileMapCollision();
	setPosition(xtemp, ytemp);	
	
	// musicNote attack
	if(firing) {
		if (charged) {
			ChargedMissile cm = new ChargedMissile(facingLeft);
			cm.setTileMap(getTileMap());
			mx = x;
			my = y;
			cm.setPosition((int) mx, (int) my);
			chargedMissiles.add(cm);
			charged = false;
			firing = false;
			currentAction = IDLE;
			animation.setFrames(sprites.get(IDLE));
			animation.setDelay(700);
		} else if (!charging){
			sfx.get("missile").play();		
			MusicNote ms = new MusicNote(facingLeft);
			ms.setTileMap(getTileMap());
			mx = x;
			my = y;
			ms.setPosition((int) mx, (int) my);
			musicNotes.add(ms);
			fire += 1;
			firing = false;
		}
	}
		
		
	// update musicNotes
	for(int i = 0; i < musicNotes.size(); i++) {
	
		musicNotes.get(i).update();						// That crazy stuff is to find out if the note is not on screen, but with the xmap from player, else it wont work :(
		if(musicNotes.get(i).shouldRemove() || (musicNotes.get(i).x + xmap + musicNotes.get(i).width < 0 || musicNotes.get(i).x + xmap - musicNotes.get(i).width > GamePanel.WIDTH)) {
			musicNotes.remove(i);
			i--;
			fire--;
		}
	}
	
	for (int i = 0; i < chargedMissiles.size(); i++) {
		chargedMissiles.get(i).update();
		if(chargedMissiles.get(i).shouldRemove() || (chargedMissiles.get(i).x + xmap + chargedMissiles.get(i).width < 0 || chargedMissiles.get(i).x + xmap - chargedMissiles.get(i).width > GamePanel.WIDTH)) {
			chargedMissiles.remove(i);
			i--;
		}
	}
	
	// check done charging
	if (charging) {
		long elapsed = (System.nanoTime() - chargeTimer) / 1000000;
		if (elapsed > 3000) {
			charged = true;
			charging = false;
			animation.setDelay(100);
		}
	}
	
	// check done flinching
	if(flinching) {
		long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
		if(elapsed > 1000) {
			flinching = false;
		}
	}

	// set animation
	if(!isDead()) {
	if (!((charging) || charged)) {
	if(dy > 0) {
		
		if(currentAction != FALLING) {
			currentAction = FALLING;
			animation.setFrames(sprites.get(FALLING));
			animation.setDelay(180);
		}
	}
	else if(dy < 0) {
		if(currentAction != JUMPING) {
			currentAction = JUMPING;
			animation.setFrames(sprites.get(JUMPING));
			animation.setDelay(180);
		}
	}
	else if(left && right) {
		if(currentAction !=IDLE) {
			currentAction = IDLE;
			animation.setFrames(sprites.get(IDLE));
			animation.setDelay(400);
		}
	}
	else if(left || right) {
		if(currentAction != WALKING) {
			currentAction  = WALKING;
			animation.setFrames(sprites.get(WALKING));
			animation.setDelay(40);
		}
	}
	else if(currentAction != IDLE) {
			currentAction = IDLE;
			animation.setFrames(sprites.get(IDLE));
			animation.setDelay(400);
	}
	}
	}
	if (isDead()) {
		if (currentAction != DYING) {
			currentAction = DYING;
			animation.setFrames(sprites.get(DYING));
			animation.setDelay(80);
		}
		if (currentAction == DYING && animation.getFrame() == 8) {
			animation.setFrame(9);
			animation.setDelay(9000);
		}
		
		
	}
	
	
	animation.update();
	
	// set direction
	if(currentAction != DYING) {
		if(right) facingLeft = true;
		if(left) facingLeft = false;
	}
	
	
	
	}

	public void draw(Graphics2D g) {
	
		setMapPosition();								// very important
	
		// draw musicNotes
		for(int i = 0; i < musicNotes.size(); i++) {
			musicNotes.get(i).draw(g);
		}
		
		// draw chargedMissiles
		for (int i = 0; i < chargedMissiles.size(); i++) {
			chargedMissiles.get(i).draw(g);
		}
	
		// draw Player
		if(flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) {
				return;
			}
		}
	
		super.draw(g);
	
	}
	
	public void updateAnimation() {
		animation.update();
		width += 10;
		height += 10;
	}
	
	public boolean isDead() { return dead; }
	
	public void setSpeed(double s, double ms) {
		moveSpeed = s;
		maxSpeed = ms;
	}

	

}
