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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import Entity.Awesome;
import Entity.Enemy;
import Entity.Coin;
import Entity.GoldenMN;
import Entity.HeartCage;
import Entity.InfoBox;
import Entity.LevelEndLove;
import Entity.Love;
import Entity.Rainbow;
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
	private String bestTime;
	private String bestNumLove;
	private String bestNumCoins;
	private boolean levelEnd;
	
	private Timer gmnMissileSpawner;
	
	Font titleFont;
	Font infoFont;
	
	// Level Ending
	private boolean fadingPink = true;
	private float fading = 0;
	private double green;
	private boolean fadingFinished;
	private Image coin;
	private Image love;
	private boolean fadeOut;
	
	private Awesome awesome;
	private ArrayList<Rainbow> rainbows;
	
	private boolean spaceKeyAvailable = true;
	
	public CenterState(GameStateManager gsm) {
		
		this.gsm = gsm;
		
		init();
		
	}
	
	
	public void init() {
				
		tileMap = new TileMap(20);
		tileMap.loadTiles("/Tilesets/CenterTileSet.gif");
		tileMap.loadMap("/Maps/Cave/c1.txt");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);
		
		bg = new Background("/Backgrounds/CENTER.gif", 0.2, false);
		
		titleFont = new Font("Arial", Font.PLAIN, 16);
		infoFont = new Font("Arial", Font.PLAIN, 14);
		
		awesome = new Awesome();
		awesome.setVector(3, 0);
		rainbows = new ArrayList<Rainbow>();
		
		player = new Player(tileMap);
		player.setPosition(40, 80);
		
		spawnHearts();
		populateEnemies();
		spawnCoins();
		spawnGMNBoxes();
		
		levelEndLove = new LevelEndLove(tileMap);
		levelEndLove.setPosition(2150, 260);
		heartCage = new HeartCage(tileMap);
		heartCage.setPosition(2150, 270);
		try {
			altar = ImageIO.read(getClass().getResource("/Sprites.Player/heartAltar.gif"));
			coin = ImageIO.read(getClass().getResource("/HUD/Coin.gif"));
			BufferedImage loveSheet = ImageIO.read(getClass().getResourceAsStream("/Sprites.Player/levelEndLove.gif"));
			love = loveSheet.getSubimage(0, 0, 24, 21);
			bestTime = GamePanel.getProperty("c1Time");
			bestNumLove = GamePanel.getProperty("c1NumLove");
			bestNumCoins = GamePanel.getProperty("c1NumCoins");
			
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
				new Point(870, 310),
				new Point(1610, 310),
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
				new Point(670, 210),
				new Point(800, 210),
				new Point(1070, 30)
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
				new Point(350, 290),
				new Point(1070, 30),
				new Point(1630, 250)
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
				new Point(1070, 250),
				new Point(1330, 30),
				new Point(2070,130),
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
		
		if (!heartCage.isDead()) {
		if(!infoBox.isDisplayed()) {
	
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
		if (player.isDead()) {
			fadeOut = true;
		}
		
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
		
		if(heartCage.isDead()) {
			centerTimer.stop();
			tileMap.setMapTile(106, 11, 0);
			tileMap.setMapTile(106, 12, 0);
			tileMap.setMapTile(106, 13, 0);
			tileMap.setMapTile(106, 14, 0);
			tileMap.setMapTile(106, 15, 0);
			tileMap.setMapTile(107, 11, 0);
			tileMap.setMapTile(107, 12, 0);
			tileMap.setMapTile(107, 13, 0);
			tileMap.setMapTile(108, 11, 0);
			tileMap.setMapTile(108, 12, 0);
			tileMap.setMapTile(108, 13, 0);
			tileMap.setMapTile(108, 14, 0);
			tileMap.setMapTile(108, 15, 0);
			int col = player.getX() / tileMap.getTileSize() - 14;
			for (int i = 0; i < tileMap.getHeight(); i++) {
				for (int j = 0; j < 10; j++) {
					tileMap.setMapTile(col - j, i, 20);
				}
			}
		}

		
		levelEnd = levelEndLove.levelEnd();
		if (awesome.getX() != player.getX()) {
			awesome.setPosition(player.getX(), player.getY());
		}
		
		
		}
		} else {
			if(!levelEndLove.isCollected()) {
				bg.setPosition(tileMap.getX(), tileMap.getY());				
				player.update();
				player.checkLevelEnd(levelEndLove);
				awesome.setPosition(player.getX() + player.getXMap() -20, player.getY() + player.getYMap()-20);
			}
			levelEndLove.update();levelEnd = levelEndLove.levelEnd();
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
		if(!levelEndLove.isCollected()) levelEndLove.draw(g);
		heartCage.draw(g);
			
		// draw InfoBox
		infoBox.draw(g);
		
		// draw hearts not collected
		for(int i = 0; i < hearts.size(); i++) {
			if(!hearts.get(i).isCollected())	hearts.get(i).draw(g);
		}
		
		// draw Player behind tileMap when alive
		if(!player.isDead()) player.draw(g);
		
		//********************Above components are drawn behind tileMap***********************//
				
		// draw tilemap
		tileMap.draw(g);
		
		// draw Player dead
		if(player.isDead()) player.draw(g);
		
		// draw hearts collected
		for(int i = 0; i < hearts.size(); i++) {
			if	(hearts.get(i).isCollected()) hearts.get(i).draw(g);
		}
		
		// draw levelEndLove collected
		if(levelEndLove.isCollected()) levelEndLove.draw(g);
		
		// draw HUD
		hud.draw(g);
			// Time
			g.setColor(Color.white);
			g.setFont(infoFont);
			g.drawString(timeString, 5, 15);
			hud.coinsFound(3-coins.size());
			
		
		
	} else {
		// Level End
		if(!fadingFinished) {
			
			if(fadingPink) {
				player.draw(g);
				//fading in pink
				g.setColor(new Color(255, 0, 255));
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fading));
				g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
				fading += 0.006;
				if (fading > 1) {
					fading = 0;
					fadingPink = false;
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			} else {
				//fading in white
				green++;
				if (green > 255) {
					green = 255;
					fadingFinished = true;
				}
				g.setColor(new Color(255, (int) green, 255));
				g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
				
				// Green is to fast to get to 255, but maybe as expected with music!
			}
		} else if (!fadeOut) {
			g.setColor(Color.white);
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
			// show level stats
			g.setFont(titleFont);
			g.setColor(Color.black);
			g.drawString("Level Stats", 120, 50);
			g.drawLine(110, 56, 210, 56);
			g.setFont(infoFont);
			g.drawString("Best", 240, 90);
			
			// Love
			g.drawImage(love, 80, 120, null);
			g.drawString("x " + player.getNumHearts(), 130, 140);
			if(player.getNumHearts() > Integer.parseInt(bestNumLove)) {
				g.setColor(Color.red);
				g.drawString("High", 240, 140);
				g.setColor(Color.blue);
				g.drawString("score", 271, 140);
				g.setColor(Color.green);
				g.drawString("!!!", 312, 140);
			} else g.drawString(bestNumLove, 240, 140);
			
			// coins
			g.setColor(Color.black);
			g.drawImage(coin, 84, 165, null);
			g.drawString("x " + (3 - coins.size()), 130, 180);
			if((3 - coins.size() > Integer.parseInt(bestNumCoins))) {
				g.setColor(Color.red);
				g.drawString("High", 240, 180);
				g.setColor(Color.blue);
				g.drawString("score", 271, 180);
				g.setColor(Color.green);
				g.drawString("!!!", 312, 180);
			} else g.drawString(bestNumCoins, 240, 180);
			
			// Time
			g.setColor(Color.black);
			g.drawString("Time: ", 80, 220);
			g.drawString(centerTime + " sec", 130, 220);
			if(Integer.parseInt(timeString) < Integer.parseInt(bestTime)) {
				g.setColor(Color.red);
				g.drawString("High", 240, 220);
				g.setColor(Color.blue);
				g.drawString("score", 271, 220);
				g.setColor(Color.green);
				g.drawString("!!!", 312, 220);
			} else g.drawString(bestTime + " sec", 240, 220);
			
			
			
		}
		
		// draw Awesome Smiley
		awesome.update();
		rainbows.add(new Rainbow(awesome.getX(), awesome.getY()));
		for (int i = 0; i < rainbows.size(); i++) {
			Rainbow r = rainbows.get(i);
			r.update();
			if(r.shouldRemove()) rainbows.remove(r);
				else r.draw(g);
		}
		awesome.draw(g);
		
	}
	
	if (fadeOut) {
		player.updateAnimation();
		if(levelEnd) {
			g.setColor(Color.white);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fading));
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			g.setColor(Color.black);
			g.drawString("saving..", 300, 250);
			
		}
		fading += 0.006;
		if(fading > 1) {
			gsm.setState(10);
		}
		// draw Awesome Smiley
		awesome.update();
		rainbows.add(new Rainbow(awesome.getX(), awesome.getY()));
		for (int i = 0; i < rainbows.size(); i++) {
			Rainbow r = rainbows.get(i);
			r.update();
		if(r.shouldRemove()) rainbows.remove(r);
			else r.draw(g);
		}
		awesome.draw(g);
	}
		
	}

	public void keyPressed(int k) {
		
		if(!gsm.isGamePaused() && !infoBox.isDisplayed() && !player.isDead()) {
		
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

			if(k == KeyEvent.VK_SHIFT) player.setSpeed(6, 6);

			
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
			if(fadingFinished) endLevel();
		}
		
		if(k == KeyEvent.VK_SHIFT) player.setSpeed(0.7, 2.7);
		
		
	}
	
	public void endLevel() {
		fading = 0;
		fadeOut = true;
		
		try {
			GamePanel.setSaveProperty("c2", Integer.toString(1));
			if(centerTime < Integer.parseInt(bestTime)) 	
				GamePanel.setSaveProperty("c1Time", String.valueOf(centerTime));
			if(player.getNumHearts() > Integer.parseInt(bestNumLove))	
				GamePanel.setSaveProperty("c1NumLove", Integer.toString(player.getNumHearts()));
			if((3 - coins.size() > Integer.parseInt(bestNumCoins)))
				GamePanel.setSaveProperty("c1NumCoins", Integer.toString(3 - coins.size()));
		} catch (IOException e) {
			e.printStackTrace();
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
