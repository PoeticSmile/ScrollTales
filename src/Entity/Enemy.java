package Entity;

import TileMap.TileMap;

public class Enemy extends MapObject{
	
	protected int health;
	protected int maxHealth;
	protected boolean dead;
	protected int damage;
	
	protected boolean spawnedHeart;
	protected boolean remove;
	
	protected boolean flinching;
	protected long flinchTimer;
	
	public Enemy() {}
	
	public boolean isDead() { return dead; }
	public boolean shouldRemove() { return remove; }
	
	public void didSpawnHeart() { spawnedHeart = true; }
	public boolean spawnedHeart() { return spawnedHeart; }
	
	public int getDamage() { return damage; }
	
	public boolean hasAnimationPlayedOnce() { return animation.hasPlayedOnce(); }
	
	public void hit(int damage) {
		if(dead || flinching) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) dead = true;
		flinching = true;
		flinchTimer = System.nanoTime();
	}
	
	public void update() {
		
	}

}