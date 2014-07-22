package GameState;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import Entity.Enemy;
import Entity.Coin;
import Entity.GoldenMN;
import Entity.HeartCage;
import Entity.InfoBox;
import Entity.LevelEndLove;
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
	private LevelEndLove levelEndLove;
	private HeartCage heartCage;
	private Image altar;
	
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
	private boolean levelEnd;
	
	private Timer gmnMissileSpawner;
	
	Font titleFont;
	Font infoFont;
	
	// Level Ending
	private float fading = 0;
	
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
		player.setPosition(1300, 80);
		
		spawnHearts();
		populateEnemies();
		spawnCoins();
		spawnGMNBoxes();
		
		levelEndLove = new LevelEndLove(tileMap);
		levelEndLove.setPosition(1550, 360);
		heartCage = new HeartCage(tileMap);
		heartCage.setPosition(1550, 370);
		try {
			altar = ImageIO.read(getClass().getResource("/Sprites.Player/heartAltar.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
				new Point(300, 410),
				new Point(250, 410),
				/*new Point(200, 600),
				new Point(800, 870),
				new Point(850, 870),
				new Point(900, 870),
				new Point(950, 870),
				new Point(1000, 870),
				new Point(1050, 870),
				new Point(1100, 870),
				new Point(1150, 870),
				new Point(1200, 870),*/
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
				new Point(740, 410),
				new Point(760, 410),
				new Point(780, 410)
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
				new Point(410, 370),
				new Point(700, 410),
				new Point(715, 410)
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
				new Point(30, 410),
		};
		
		for(int i = 0; i < points.length; i++) {
			b = new GoldenMN(tileMap);
			b.setPosition(points[i].x, points[i].y);
			b.setMapPosition();
			gmnBoxes.add(b);
		}
		activeGmnBoxes = gmnBoxes.size();
	}
	
	private void setInfoTexts() {
		movementInst = new String[2];
		movementInst[0] = "Use A & D to move around. ";
		movementInst[1] = "1";
		jumpingInst = new String[2];
		jumpingInst[0] = "Use SPACE to jump. ";
		jumpingInst[1] = "1";
		fireInst = new String[2];
		fireInst[0] = "Press W to fire. ";
		fireInst[1] = "1";
	}
	
	public void setMapTile(int col, int row, int tile) {
		tileMap.setMapTile(col, row, tile);
	}

	public void update() {
		
		if(!infoBox.isDisplayed() || !levelEnd) {
	
		// update player
		if(!levelEndLove.isCollected()) {
			player.checkGoldenMN(gmnBoxes);
			player.checkHeartCageAttack(heartCage);
			player.update();
			tileMap.setPosition(GamePanel.WIDTH / 2 - player.getX(), GamePanel.HEIGHT / 2 - player.getY());
		}
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
				hearts.get(hearts.size()-1).initSpawning();
				b.cantSpawnHeart();
				activeGmnBoxes--;
			}
		}
		
		heartCage.update();
		levelEndLove.update();
		
		// update hearts
		for(int i = 0; i < hearts.size(); i++) {
			hearts.get(i).update();
		}
		
		// update collection stuff
		player.checkLove(hearts);
		player.checkLevelEnd(levelEndLove);
		player.checkCoin(coins);
		player.setNumEnemies(enemies.size() + activeGmnBoxes);
		
		// update infoBox
		/*	// movement Instructions
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
		*/			
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
		
		if(heartCage.isDestroyed()) {
			System.out.println("Setting tiles");
			tileMap.setMapTile(76, 16, 0);
			tileMap.setMapTile(76, 17, 0);
			tileMap.setMapTile(76, 18, 0);
			tileMap.setMapTile(76, 19, 0);
			tileMap.setMapTile(76, 20, 0);
			tileMap.setMapTile(77, 16, 0);
			tileMap.setMapTile(77, 17, 0);
			tileMap.setMapTile(77, 18, 0);
			tileMap.setMapTile(78, 16, 0);
			tileMap.setMapTile(78, 17, 0);
			tileMap.setMapTile(78, 18, 0);
			tileMap.setMapTile(78, 19, 0);
			tileMap.setMapTile(78, 20, 0);
		}
		levelEnd = levelEndLove.levelEnd();
		if(levelEnd) {
			centerTimer.stop();
			
		}
		
		}
		
		
		
	}

	public void draw(Graphics2D g) {
		
	if(!levelEnd) {
		
		// draw bg
		bg.draw(g);
		
		// draw coins
		for(int i = 0; i < coins.size(); i++) {
			coins.get(i).draw(g);
		}
		
		// draw tilemap
		tileMap.draw(g);
		
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
		
		// draw Level End components
		g.drawImage(altar,(int) (heartCage.getX() + tileMap.getX())-30, (int) (heartCage.getY() + tileMap.getY()) - 10, null);
		levelEndLove.draw(g);
		heartCage.draw(g);
			
		// draw InfoBox
		infoBox.draw(g);
		
		// draw hearts
		for(int i = 0; i < hearts.size(); i++) {
			hearts.get(i).draw(g);
		}
		
	} else {
		// Level End
		g.setColor(Color.white);
		if(fading < 0) fading = 0;
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fading));
		g.fillRect(40, 40, GamePanel.WIDTH, GamePanel.HEIGHT);
		fading -= 0.004;
	}
	
	// draw Player
	player.draw(g);
	
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
			
			if(k == KeyEvent.VK_T) tileMap.setMapTile(74, 19, 17);
			if(k == KeyEvent.VK_Z) tileMap.setMapTile(75, 19, 18);

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
