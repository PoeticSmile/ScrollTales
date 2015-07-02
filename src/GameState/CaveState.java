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
import Entity.GoldenMN;
import Entity.HeartCage;
import Entity.LevelEndLove;
import Entity.Love;
import Entity.MusicBox;
import Entity.MusicBoxSet;
import Entity.Rainbow;
//import Entity.Explosion;
import Entity.HUD;
import Entity.Player;
import Entity.Enemies.Crawler;
import Entity.MapComponents.Coin;
import Entity.MapComponents.Door;
import Entity.MapComponents.InfoBox;
import Entity.MapComponents.Sign;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;
import Audio.AudioPlayer;

public class CaveState extends GameState implements ActionListener{
	
	private ArrayList<TileMap> maps;
	private TileMap mainMap;
	private TileMap testMap;
	private TileMap currMap;
	
	private static int MAP_INDEX_CURRENT;
	private static final int MAP_INDEX_MAIN = 0;
	private static final int MAP_INDEX_TESTROOM = 1;
	
	private Player player;
	private Timer gmnBoxMissileSpawner;
	private HUD hud;
	int numCoins = 0;
	private Font infoFont;
	private Font titleFont;
	
	private AudioPlayer theme;
	
	private Timer centerTimer;
	private long centerTime = 0;
	private String timeString;
	private String bestTime;
	private String bestNumLove;
	private String bestNumCoins;
	private boolean levelEnd;
	
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
	
	public CaveState(GameStateManager gsm) {
		this.gsm = gsm;
		
		init();
	}
	
	
	public void init() {
		
		maps = new ArrayList<TileMap>();
		
		player = new Player();
		player.setPosition(40, 80);
		setupMainMap();
		
		testMap = new TileMap(20, false);
		testMap.loadTiles("/Tilesets/CenterTileSet.gif");
		testMap.loadMap("/Maps/TESTROOM.txt");
		testMap.setPosition(0, 0);
		testMap.setTween(1);
		testMap.setBackground(mainMap.getBackground());
		testMap.setPlayer(player);
		GoldenMN gmn = new GoldenMN();
		gmn.setPosition(70, 210);
		ArrayList<GoldenMN> gmns = new ArrayList<GoldenMN>();
		gmns.add(gmn);
		testMap.setGMNBoxes(gmns);
		
		// doors
		Door testDoorSTART = new Door(false, false);
		testDoorSTART.setMapChangePath("MAIN_TESTROOM");
		testDoorSTART.setPosition(290, 300);
		ArrayList<Door> mainMapDoors = new ArrayList<Door>();
		mainMapDoors.add(testDoorSTART);
		mainMap.setDoors(mainMapDoors);
		
		Door testDoorTARGET = new Door(false, false);
		testDoorTARGET.setMapChangePath("TESTROOM_MAIN");
		testDoorTARGET.setPosition(90, 260);
		ArrayList<Door> testMapDoors = new ArrayList<Door>();
		testMapDoors.add(testDoorTARGET);
		testMap.setDoors(testMapDoors);
		
		maps.add(mainMap);
		maps.add(testMap);
		currMap = maps.get(MAP_INDEX_MAIN);
		MAP_INDEX_CURRENT = MAP_INDEX_MAIN;
		player.setTileMap(currMap);
		currMap.fadeOut();
		currMap.setFadeColor(Color.white);
		
		awesome = new Awesome();
		awesome.setVector(3, 0);
		rainbows = new ArrayList<Rainbow>();
		
		
		try {
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
		titleFont = new Font("Arial", Font.PLAIN, 16);
		infoFont = new Font("Arial", Font.PLAIN, 14);
		
		theme = new AudioPlayer("/Music/menuTheme.wav");
		//theme.play();
		
		centerTimer = new Timer(1000, this);
		centerTimer.start();
		
		gmnBoxMissileSpawner = new Timer(5000, this);
		gmnBoxMissileSpawner.start();
		
		
	}
	
	private void setupMainMap() {
		
		mainMap = new TileMap(20, true);
		mainMap.loadTiles("/Tilesets/CenterTileSet.gif");
		mainMap.loadMap("/Maps/Cave/c1.txt");
		mainMap.setPosition(0, 0);
		mainMap.setTween(1);
		
		// fill it with stuff
		// setup background
		Background bg = new Background("/BAckgrounds/CENTER.gif", 0.2, false);
		mainMap.setBackground(bg);
		
		// setup textFont
		Font textFont = new Font("Arial", Font.PLAIN, 14);
		mainMap.setTextFont(textFont);
		
		// setup hearts
		ArrayList<Love> hearts = new ArrayList<Love>();
		Love heart;
		Point[] heartPoint = new Point[] {
				new Point(870, 310),
				new Point(1610, 310),
		};
		for(int i = 0; i < heartPoint.length; i++) {
			heart = new Love();
			heart.setPosition(heartPoint[i].x, heartPoint[i].y);
			hearts.add(heart);
		}
		mainMap.setHearts(hearts);
		
		// setup enemies
		ArrayList<Enemy> enemies = new ArrayList<Enemy>();
		// gmnBoxes
		ArrayList<GoldenMN> gmnBoxes = new ArrayList<GoldenMN>();
		GoldenMN b;
		Point[] gmnBoxesPoint = new Point[] {
				new Point(1070, 250),
				new Point(1330, 30),
				new Point(2070,130),
		};
		
		for(int i = 0; i < gmnBoxesPoint.length; i++) {
			b = new GoldenMN();
			b.setPosition(gmnBoxesPoint[i].x, gmnBoxesPoint[i].y);
			gmnBoxes.add(b);
		}
		int activeGmnBoxes = gmnBoxes.size();
		mainMap.setGMNBoxes(gmnBoxes);
		mainMap.setActiveGMNBoxes(activeGmnBoxes);
		
		// crawler
		Crawler craw;
		Point[] crawlerPoint = new Point[] {
				new Point(400, 170),
				new Point(670, 210),
				new Point(800, 210),
				new Point(1070, 30)
		};
		for(int i = 0; i < crawlerPoint.length; i++) {
			craw = new Crawler();
			craw.setPosition(crawlerPoint[i].x, crawlerPoint[i].y);
			enemies.add(craw);
		}
		
		mainMap.setEnemies(enemies);
		
		// setup coins
		ArrayList<Coin> coins = new ArrayList<Coin>();
		Coin coin;
		Point[] coinPoint = new Point[] {
				new Point(350, 290),
				new Point(1070, 30),
				new Point(1630, 250)
		};
		
		for(int i = 0; i < coinPoint.length; i++) {
			coin = new Coin();
			coin.setPosition(coinPoint[i].x, coinPoint[i].y);
			coins.add(coin);
		}
		mainMap.setCoins(coins);
		
		// setup musicBoxSets
		MusicBoxSet musicBoxSet1 = new MusicBoxSet(4, 1030, 310);
		MusicBoxSet musicBoxSet2 = new MusicBoxSet(1, 1830, 310);
		ArrayList<MusicBoxSet> musicBoxSets = new ArrayList<MusicBoxSet>();
		musicBoxSets.add(musicBoxSet1);
		musicBoxSets.add(musicBoxSet2);
		mainMap.setMusicBoxSets(musicBoxSets);
		
		// levelEnd components
		LevelEndLove lel = new LevelEndLove();
		lel.setPosition(2150, 260);
		HeartCage hc = new HeartCage();
		hc.setPosition(2150, 270);
		try {
			Image altar = ImageIO.read(getClass().getResource("/Sprites.Player/heartAltar.gif"));
			mainMap.setAltar(altar);
		} catch(Exception e) { e.printStackTrace(); }
		mainMap.setLevelEndLove(lel);
		mainMap.setHeartCage(hc);
		
		// signs
		ArrayList<Sign> signs = new ArrayList<Sign>();
		String instString = "Use A, D and SPACE to move around.";
		Sign instSign = new Sign(instString, true, null);
		instSign.setPosition(90, 310);
		signs.add(instSign);
		mainMap.setSigns(signs);
		
		mainMap.setPlayer(player);
		
	}
	
	public void setMap(int index) {
		currMap.finishFading();
		clearMap();
		MAP_INDEX_CURRENT = index;
		player.setTileMap(maps.get(index));
		currMap = maps.get(MAP_INDEX_CURRENT);
		currMap.fadeOut();
	}
	
	public void clearMap() {
		// clear map from missiles as they would hurt player when going back to previous map
		if (currMap != null) {
			if (currMap.getGMNBoxes() != null) {
					for (int i = 0; i < currMap.getGMNBoxes().size(); i++) currMap.getGMNBoxes().get(i).clearBadMusicNotes();
			}
		}
		player.clearMusicNotes();
	}

	public void update() {
		
		currMap.update();
		levelEnd = currMap.levelEnd();
		currMap.setPosition(GamePanel.WIDTH / 2 - player.getX(), GamePanel.HEIGHT / 2 - player.getY());
		
		if ((!currMap.containsLevelEnd()) || (currMap.containsLevelEnd() && !currMap.getHeartCage().isDead())) {
			if(!currMap.isSignBeingRead()) {
			
				// update player
				if((!currMap.containsLevelEnd()) || (currMap.containsLevelEnd() && !currMap.getLevelEndLove().levelEnd())) {
					if (currMap.getGMNBoxes() != null) player.checkGoldenMN(currMap.getGMNBoxes());
					if (currMap.getMusicBoxSets() != null) {
						for (int i = 0; i < currMap.getMusicBoxSets().size(); i++) player.checkMusicBox(currMap.getMusicBoxSets().get(i).getMusicBoxes());
					}
					if (currMap.containsLevelEnd()) player.checkHeartCageAttack(currMap.getHeartCage());
					if (currMap.getSigns() != null) player.checkSigns(currMap.getSigns());
					if (currMap.getDoors() != null) player.checkDoors(currMap.getDoors());
					player.update();
					
				}
		
				// attack enemie
				if (currMap.getEnemies() != null) player.checkAttack(currMap.getEnemies());
				if (currMap.getGMNBoxes() != null) {
					for(int i = 0; i < currMap.getGMNBoxes().size(); i++) {
					player.checkBadMusicNotes(currMap.getGMNBoxes().get(i).getBadMusicNotes());
					}
				}
				
				// update collection stuff
				if (currMap.getHearts() != null) player.checkLove(currMap.getHearts());
				if (currMap.containsLevelEnd()) player.checkLevelEnd(currMap.getLevelEndLove());
				if (currMap.getCoins() != null) player.checkCoin(currMap.getCoins());
				int numEnemies = 0;
				numCoins = 0;
				for (int i = 0; i < maps.size(); i++) {
					if (maps.get(i).getEnemies() != null) numEnemies += maps.get(i).getNumEnemies();
					if (maps.get(i).getGMNBoxes() != null) numEnemies += maps.get(i).getNumActiveGMNBoxes();
					if (maps.get(i).getCoins() != null) numCoins += maps.get(i).getNumCoins();
				}
				player.setNumEnemies(numEnemies);
				if (player.isDead()) {
					fadeOut = true;
				}
				
				// check map change through door
				ArrayList<Door> currMapDoors = currMap.getDoors();
				if (currMapDoors != null) {
					for (int i = 0; i < currMapDoors.size(); i++) {
						if (currMapDoors.get(i).isDoorOpen()) {
							Door d = currMapDoors.get(i);
							if (d.mapChangePath() == "MAIN_TESTROOM") {
								setMap(MAP_INDEX_TESTROOM);
								for (int j = 0; j < testMap.getDoors().size(); j++) {
									if (testMap.getDoors().get(j).mapChangePath() == "TESTROOM_MAIN") {
										player.setPosition(testMap.getDoors().get(j).getX(), testMap.getDoors().get(j).getY()+10);
										continue;
									}
								}
								player.setDoorOpening(false);
								d.closeDoor();
							} else if (d.mapChangePath() == "TESTROOM_MAIN") {
								setMap(MAP_INDEX_MAIN);
								for (int j = 0; j < mainMap.getDoors().size(); j++) {
									if (mainMap.getDoors().get(j).mapChangePath() == "MAIN_TESTROOM") {
										player.setPosition(mainMap.getDoors().get(j).getX(), mainMap.getDoors().get(j).getY()+10);
										continue;
									}
								}
								player.setDoorOpening(false);
								d.closeDoor();
							}
							
						}
					}
				}

				if(gsm.isGamePaused()) {
					gsm.setGamePaused(true);
					centerTimer.stop();
					player.setLeft(false);
					player.setRight(false);
					player.setDown(false);
					player.setJumping(false);	// turned off because of uncontrolable jump after pause
				}
				if(currMap.isSignBeingRead()) {
					centerTimer.stop();
					player.setLeft(false);
					player.setRight(false);
					player.setDown(false);
					player.setJumping(false);	// turned off because of uncontrolable jump after pause
				}
		
				if(currMap.containsLevelEnd()) {
					if (currMap.getHeartCage().isDead()) {
						centerTimer.stop();
						clearMap();
						currMap.setMapTile(106, 11, 0);
						currMap.setMapTile(106, 12, 0);
						currMap.setMapTile(106, 13, 0);
						currMap.setMapTile(106, 14, 0);
						currMap.setMapTile(106, 15, 0);
						currMap.setMapTile(107, 11, 0);
						currMap.setMapTile(107, 12, 0);
						currMap.setMapTile(107, 13, 0);
						currMap.setMapTile(108, 11, 0);
						currMap.setMapTile(108, 12, 0);
						currMap.setMapTile(108, 13, 0);
						currMap.setMapTile(108, 14, 0);
						currMap.setMapTile(108, 15, 0);
						int col = player.getX() / currMap.getTileSize() - 14;
						for (int i = 0; i < currMap.getHeight(); i++) {
							for (int j = 0; j < 10; j++) {
								currMap.setMapTile(col - j, i, 20);
							}
						}
						if (awesome.getX() != player.getX()) {
							awesome.setPosition(player.getX(), player.getY());
						}
					}
				}
				
			
		
		}
		} else {
			if(currMap.containsLevelEnd()) {
				if (!currMap.getLevelEndLove().isCollected()) {		
					player.update();
					if (currMap.getCoins() != null) player.checkCoin(currMap.getCoins());
					if (currMap.getHearts() != null) player.checkLove(currMap.getHearts());
					player.checkLevelEnd(currMap.getLevelEndLove());
					awesome.setPosition(-60, 80);
				} else {
					levelEnd = currMap.getLevelEndLove().levelEnd();
					
				}
				currMap.getLevelEndLove().update();
				if (currMap.getHearts() != null) for (int i = 0; i < currMap.getHearts().size(); i++) currMap.getHearts().get(i).update();
				if (currMap.getCoins() != null) for (int i = 0; i < currMap.getCoins().size(); i++) currMap.getCoins().get(i).update();
			}
		}
		
		
	}

	public void draw(Graphics2D g) {
		
	if(!levelEnd) {
		
		// draw tilemap
		currMap.draw(g);
		
		// draw HUD
		hud.draw(g);
			// Time
			g.setColor(Color.white);
			g.setFont(infoFont);
			if (timeString != null) g.drawString(timeString, 5, 15);
			hud.coinsFound(3-numCoins);
			
		
	} else {
		
		// Level End
		if(!fadingFinished) {
			
			if(fadingPink) {
				player.draw(g);
				//fading in pink
				g.setColor(new Color(255, 0, 255));
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fading));
				g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
				fading += 0.05;
				if (fading > 1) {
					fading = 0;
					fadingPink = false;
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			} else {
				//fading in white
				green += 2;
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
			g.drawString("Level Stats", 120, 30);
			g.drawLine(110, 36, 210, 36);
			g.setFont(infoFont);
			g.drawString("Best", 240, 70);
			
			// Love
			g.drawImage(love, 80, 100, null);
			g.drawString("x " + player.getNumHearts(), 130, 120);
			if(player.getNumHearts() > Integer.parseInt(bestNumLove)) {
				g.setColor(Color.red);
				g.drawString("High", 240, 120);
				g.setColor(Color.blue);
				g.drawString("score", 271, 120);
				g.setColor(Color.green);
				g.drawString("!!!", 312, 120);
			} else g.drawString(bestNumLove, 240, 120);
			
			// coins
			g.setColor(Color.black);
			g.drawImage(coin, 84, 145, null);
			g.drawString("x " + (3 - numCoins), 130, 160);
			if((3 - numCoins > Integer.parseInt(bestNumCoins))) {
				g.setColor(Color.red);
				g.drawString("High", 240, 160);
				g.setColor(Color.blue);
				g.drawString("score", 271, 160);
				g.setColor(Color.green);
				g.drawString("!!!", 312, 160);
			} else g.drawString(bestNumCoins, 240, 160);
			
			// Time
			g.setColor(Color.black);
			g.drawString("Time: ", 80, 200);
			g.drawString(centerTime + " sec", 130, 200);
			if(Integer.parseInt(timeString) < Integer.parseInt(bestTime)) {
				g.setColor(Color.red);
				g.drawString("High", 240, 200);
				g.setColor(Color.blue);
				g.drawString("score", 271, 200);
				g.setColor(Color.green);
				g.drawString("!!!", 312, 200);
			} else g.drawString(bestTime + " sec", 240, 200);
			
			
			
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
		
		if(!gsm.isGamePaused() && !currMap.isSignBeingRead() && !player.isOpeningDoor() && !player.isDead()) {
		
			if(k == KeyEvent.VK_A) player.setLeft(true);
		
			if(k == KeyEvent.VK_D) player.setRight(true);
		
			if(k == KeyEvent.VK_S) player.charge();
		
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
		
		if(!gsm.isGamePaused() && !currMap.isSignBeingRead() && !player.isOpeningDoor()) {
		
			if(k == KeyEvent.VK_A) player.setLeft(false);
		
			if(k == KeyEvent.VK_D) player.setRight(false);
		
			if(k == KeyEvent.VK_S) player.chargeEnded();
			
			if(k == KeyEvent.VK_E) {
				if (!player.isDead() && currMap.getDoors() != null) {
					for (int i = 0; i < currMap.getDoors().size(); i++) {
						if (currMap.getDoors().get(i).intersects(player)) {
							currMap.getDoors().get(i).openDoor();
							player.setDoorOpening(true);
							currMap.fadeIn();
						}
					}
				}
			}
		
			if(k == KeyEvent.VK_SPACE) player.setJumping(false);	spaceKeyAvailable = true;
			
		}
		
		if(k == KeyEvent.VK_ENTER) {
			if (currMap.getSigns() != null) {
				for (int i = 0; i < currMap.getSigns().size(); i++) {
					if (currMap.getSigns().get(i).intersects(player)) {
						currMap.getSigns().get(i).enterKeyWasReleased();
						currMap.setSignIsBeingRead(currMap.getSigns().get(i).isBeingRead());
					}
				}
			}
			
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
			if((3 - numCoins > Integer.parseInt(bestNumCoins)))
				GamePanel.setSaveProperty("c1NumCoins", Integer.toString(3 - numCoins));
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
		
		if(!currMap.isSignBeingRead() && !player.isOpeningDoor()) {
			
			// stage Time
			if(e.getSource().equals(centerTimer)){
				centerTime += 1;
				timeString = String.format("%03d", centerTime);
			}
		
			// gmn Missile Spawner
			if(e.getSource().equals(gmnBoxMissileSpawner)) {
				maps.get(MAP_INDEX_CURRENT).GMNBoxShouldCharge();
			}
		}
	}
	

}