package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.Timer;

import Entity.Enemy;
import Entity.Coin;
import Entity.GoldenMN;
import Entity.InfoBox;
import Entity.Love;
//import Entity.Explosion;
import Entity.HUD;
import Entity.Player;
import Entity.Enemies.Crawler;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;
import Audio.AudioPlayer;

public class CenterState extends GameState implements ActionListener{
	
	private TileMap tileMap;
	private Background bg;
	
	private Player player;
	
	private ArrayList<Love> hearts;
	private ArrayList<Coin> coins;
	private ArrayList<GoldenMN> gmnBoxes;
	private int activeGmnBoxes;
	
	private HUD hud;
	//Infoboxes
	private InfoBox infoBox;
	private String[] movementInst;
	private String[] jumpingInst;
	private String[] fireInst;
	
	private AudioPlayer theme;
	
	private ArrayList<Enemy> enemies;
	
	private Timer centerTimer;
	private long centerTime = 0;
	private String timeString;
	
	private Timer gmnMissileSpawner;
	
	Font titleFont;
	Font infoFont;
	
	private boolean spaceKeyAvailable = true;
	
	public CenterState(GameStateManager gsm) {
		
		this.gsm = gsm;
		
		init();
		
	}
	
	
	public void init() {
		
		tileMap = new TileMap(20);
		tileMap.loadTiles("/Tilesets/CenterTileSet.gif");
		tileMap.loadMap("/Maps/CenterMap1.txt");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);
		
		bg = new Background("/Backgrounds/CENTER.gif", 0.2, false);
		
		titleFont = new Font("Arial", Font.PLAIN, 16);
		infoFont = new Font("Arial", Font.PLAIN, 14);
		
		player = new Player(tileMap);
		player.setPosition(80, 600);
		
		spawnHearts();
		populateEnemies();
		spawnCoins();
		spawnGMNBoxes();
		
		hud = new HUD(player);
		infoBox = new InfoBox();
		setInfoTexts();
		
		theme = new AudioPlayer("/Music/menuTheme.wav");
		//theme.play();
		
		centerTimer = new Timer(1000, this);
		centerTimer.start();
		
		gmnMissileSpawner = new Timer(5000, this);
		gmnMissileSpawner.start();
		
		
	}
	
	private void spawnHearts() {
		
		hearts = new ArrayList<Love>();

		Love heart;
		Point[] points = new Point[] {
				new Point(300, 600),
				new Point(250, 600),
				new Point(200, 600),
				new Point(800, 870),
				new Point(850, 870),
				new Point(900, 870),
				new Point(950, 870),
				new Point(1000, 870),
				new Point(1050, 870),
				new Point(1100, 870),
				new Point(1150, 870),
				new Point(1200, 870),
		};
		for(int i = 0; i < points.length; i++) {
			heart = new Love(tileMap);
			heart.setPosition(points[i].x, points[i].y);
			hearts.add(heart);
		}
	}
	
	private void populateEnemies() {

		enemies = new ArrayList<Enemy>();
		
		Crawler craw;
		Point[] points = new Point[] {
				new Point(640, 800),
				new Point(680, 800),
				new Point(600, 800)
		};
		for(int i = 0; i < points.length; i++) {
			craw = new Crawler(tileMap);
			craw.setPosition(points[i].x, points[i].y);
			enemies.add(craw);
		}
	}
	
	private void spawnCoins() {
		
		coins = new ArrayList<Coin>();
		
		Coin coin;
		Point[] points = new Point[] {
				new Point(350, 850),
				new Point(700, 870),
				new Point(715, 870)
		};
		
		for(int i = 0; i < points.length; i++) {
			coin = new Coin(tileMap);
			coin.setPosition(points[i].x, points[i].y);
			coins.add(coin);
		}
	}
	
	private void spawnGMNBoxes() {
		
		gmnBoxes = new ArrayList<GoldenMN>();
		
		GoldenMN b;
		Point[] points = new Point[] {
				new Point(30, 870),
				new Point(850, 800),
				new Point(1550, 870)
		};
		
		for(int i = 0; i < points.length; i++) {
			b = new GoldenMN(tileMap);
			b.setPosition(points[i].x, points[i].y);
			gmnBoxes.add(b);
		}
		activeGmnBoxes = gmnBoxes.size();
	}
	
	private void setInfoTexts() {
		movementInst = new String[2];
		movementInst[0] = "Use A & D to move around. ";
		movementInst[1] = "0";
		jumpingInst = new String[2];
		jumpingInst[0] = "Use SPACE to jump. ";
		jumpingInst[1] = "0";
		fireInst = new String[2];
		fireInst[0] = "Press W to fire. ";
		fireInst[1] = "0";
	}
	

	public void update() {
		
		if(!infoBox.isDisplayed()) {
	
		// update player
		player.checkGoldenMN(gmnBoxes);
		player.update();
		tileMap.setPosition(GamePanel.WIDTH / 2 - player.getX(), GamePanel.HEIGHT / 2 - player.getY());
			
		// set background
		bg.setPosition(tileMap.getX(), tileMap.getY());				
				
		// attack enemie
		player.checkAttack(enemies);
		for(int i = 0; i < gmnBoxes.size(); i++) {
			player.checkBadMusicNotes(gmnBoxes.get(i).getBadMusicNotes());
		}
				
		// update all enemies
		for(int i = 0; i < enemies.size(); i++) {			
			Enemy e = enemies.get(i);
			e.update();
					
			// remove them if they're dead
			if(e.getCurrentAction() == 0 && e.hasAnimationPlayedOnce()) {
				// add a heart
				hearts.add(new Love(tileMap));
				hearts.get(hearts.size()-1).setPosition(e.getX(), e.getY());
				// remove enemy
				enemies.remove(i);
				i--;
			}
			
		}
		
		
		
		// update coins
		for(int i = 0; i < coins.size(); i++) {
			coins.get(i).update();
		}
		
		// update gmnBoxes
		for(int i = 0; i < gmnBoxes.size(); i++) {
			GoldenMN b = gmnBoxes.get(i);
			b.update();
			if(b.isDead() && b.canSpawnHeart()){
				// add a heart
				hearts.add(new Love(tileMap));
				hearts.get(hearts.size()-1).setPosition(b.getX(), b.getY());
				b.cantSpawnHeart();
				activeGmnBoxes--;
			}
			
		}
		
		// update hearts
		for(int i = 0; i < hearts.size(); i++) {
			hearts.get(i).update();
		}
		
		// update collection stuff
		player.checkLove(hearts);
		player.checkCoin(coins);
		player.setNumEnemies(enemies.size() + activeGmnBoxes);
		
		// update infoBox
			// movement Instructions
			if(29 < player.getX() && player.getX() < 131 && player.getY() == 870 && movementInst[1].contentEquals("1")) {
				movementInst[1] = "0";
				infoBox.fillInfoBox(movementInst[0]);
			}
			// jumping Instructions
			if(movementInst[1].contentEquals("0") && 129 < player.getX() && player.getX() < 131 && jumpingInst[1].contentEquals("1")) {
				jumpingInst[1] = "0";
				infoBox.fillInfoBox(jumpingInst[0]);
			}
			// fire Instructions
			if(fireInst[1].contentEquals("1") && 410 < player.getX() && player.getX() < 600) {
				fireInst[1] = "0";
				infoBox.fillInfoBox(fireInst[0]);
			}
					
		infoBox.update();
		
		if(gsm.isGamePaused()) {
			gsm.setGamePaused(true);
			centerTimer.stop();
			player.setLeft(false);
			player.setRight(false);
			player.setDown(false);
			player.setJumping(false);	// turned off because of uncontrolable jump after pause
		}
		if(infoBox.isDisplayed()) {
			centerTimer.stop();
			player.setLeft(false);
			player.setRight(false);
			player.setDown(false);
			player.setJumping(false);	// turned off because of uncontrolable jump after pause
		}
		
				
		// update explosions
		/*for(int i = 0; i < explosions.size(); i++) {
			explosions.get(i).update();
				
			if(explosions.get(i).shouldRemove()) {
				explosions.remove(i);
			}
		}*/
		
		}
		
	}

	public void draw(Graphics2D g) {
		
		// draw bg
		bg.draw(g);

		g.setColor(Color.white);
		g.fillRect(0, 0, tileMap.getWidth(), tileMap.getHeight());
		
		// draw coins
		for(int i = 0; i < coins.size(); i++) {
			coins.get(i).draw(g);
		}
		
		// draw tilemap
		tileMap.draw(g);
		
		// draw Player
		player.draw(g);
		
		// draw enemies
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
		}
		
		// draw gmnBoxes
		for(int i = 0; i < gmnBoxes.size(); i++) {
			gmnBoxes.get(i).draw(g);
			// give them the actual player coordinates
			if(gmnBoxes.get(i).getCurrentAction() == 1) {
				gmnBoxes.get(i).setPlayerPosition(player.getX(), player.getY());
			}
		}
			
		// draw InfoBox
		infoBox.draw(g);
		
		// draw hearts
		for(int i = 0; i < hearts.size(); i++) {
			hearts.get(i).draw(g);
		}
		
		// draw HUD
		hud.draw(g);
			// Time
			g.setColor(Color.white);
			g.setFont(infoFont);
			g.drawString(timeString, 5, 15);
			hud.coinsFound(3-coins.size());
		
		
	}

	public void keyPressed(int k) {
		
		if(!gsm.isGamePaused() && !infoBox.isDisplayed()) {
		
			if(k == KeyEvent.VK_A) player.setLeft(true);
		
			if(k == KeyEvent.VK_D) player.setRight(true);
		
			if(k == KeyEvent.VK_S) player.setDown(true);
		
			if(k == KeyEvent.VK_SPACE) {
				
				if(spaceKeyAvailable) {
				player.setJumping(true);
				player.jumpKeyPressed = true;
				spaceKeyAvailable = false;
				}
				
			}

			if(k == KeyEvent.VK_W) player.setFiring();


		}
		
		
		
	}

	public void keyReleased(int k) {
		
		if(!gsm.isGamePaused() && !infoBox.isDisplayed()) {
		
			if(k == KeyEvent.VK_A) player.setLeft(false);
		
			if(k == KeyEvent.VK_D) player.setRight(false);
		
			if(k == KeyEvent.VK_S) player.setDown(false);
		
			if(k == KeyEvent.VK_SPACE) player.setJumping(false);	spaceKeyAvailable = true;
		
		}
		
		if(k == KeyEvent.VK_ENTER) {
			if(infoBox.isDisplayed())	infoBox.enterWasPressed();
		}
		
		
	}
	
	public void stop() {
		player.setLeft(false);
		player.setRight(false);
		player.setJumping(false);
		player.setDown(false);
		centerTimer.stop();
		theme.stop();
	}
	
	public void resume() {
		centerTimer.start();
	}


	public void actionPerformed(ActionEvent e) {
		
		if(!infoBox.isDisplayed()) {
		// stage Time
		if(e.getSource().equals(centerTimer)){
			centerTime += 1;
			timeString = String.format("%03d", centerTime);
		}
		
		// gmn Missile Spawner
		if(e.getSource().equals(gmnMissileSpawner)) {
			for(int i = 0; i < gmnBoxes.size(); i++) {
				GoldenMN b = gmnBoxes.get(i);
				if(!b.notOnScreen()) {
					b.charge();
				}
			}
		}
		
		}
			
	}
	
	

}
