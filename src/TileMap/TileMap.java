package TileMap;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import Entity.Enemy;
import Entity.GoldenMN;
import Entity.HeartCage;
import Entity.LevelEndLove;
import Entity.Love;
import Entity.MusicBoxSet;
import Entity.Player;
import Entity.MapComponents.Coin;
import Entity.MapComponents.Door;
import Entity.MapComponents.Sign;
import Main.GamePanel;


	public class TileMap {
		
		// Objects
			private Background bg;
			boolean containsLevelEnd;		
			private boolean levelEnd;
			private Player player;

			// static
			private ArrayList<Love> hearts;
			private int numHearts;
			private ArrayList<Coin> coins;
			private int numCoins;
			
			private ArrayList<Sign> signs;
			private Sign instructionSign;
			private boolean signIsBeingRead;
			
			private ArrayList<Door> doors;
			private Door testDoor;
			Door TEST_ROOM_DOOR;
			// dynamic
			private ArrayList<GoldenMN> gmnBoxes;
			private int activeGMNBoxes;
			private ArrayList<Enemy> enemies;
			private int numEnemies;
			private ArrayList<MusicBoxSet> musicBoxSets;
			private LevelEndLove levelEndLove;
			private HeartCage heartCage;
			private Image altar;
			
			// map change fading
			private boolean fadeIn;
			private boolean fadeOut;
			private float fadingAlpha = 0.0f;
			private Color fadingColor;
			
		private Font textFont;
	
			
		// position
		private double x;
		private double y;
		
		// bounds
		private int xmin;
		private int ymin;
		private int xmax;
		private int ymax;
		
		private double tween = 1;
		
		// map
		private int[][] map;
		private int tileSize;
		private int numRows;
		private int numCols;
		private int width;
		private int height;

		// tileset
		private BufferedImage tileset;
		private int numTilesAcross;
		private Tile[][] tiles;
		
		// drawing
		private int rowOffset;
		private int colOffset;
		private int numRowsToDraw;
		private int numColsToDraw;
		
		public TileMap(int tileSize, boolean containsLevelEnd) {
			this.tileSize = tileSize;
			numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
			numColsToDraw = GamePanel.WIDTH / tileSize + 22;
			
			this.containsLevelEnd = containsLevelEnd;
		}
		
		
		public void loadTiles(String s) {
			
			try {
				tileset = ImageIO.read(getClass().getResourceAsStream(s));
				numTilesAcross = tileset.getWidth() / tileSize;
				tiles = new Tile[4][numTilesAcross];
				
				BufferedImage subimage;
				for(int col = 0; col < numTilesAcross; col++) {
					subimage = tileset.getSubimage(col * tileSize, 0, tileSize, tileSize);				// obere Kolonne, normal tiles
					tiles[0][col] = new Tile(subimage, Tile.NORMAL);
					subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);		// untere Kolonne, blocking tiles
					tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
					subimage = tileset.getSubimage(col * tileSize, 2 * tileSize, tileSize, tileSize);
					tiles[2][col] = new Tile(subimage, Tile.JUMPTHROUGH);
					subimage = tileset.getSubimage(col * tileSize, 3 * tileSize, tileSize, tileSize);
					tiles[3][col] = new Tile(subimage, Tile.EVENT);
			
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		
		 
		public void loadMap(String s) {
			
			try {
				
				InputStream in = getClass().getResourceAsStream(s); 
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				numCols = Integer.parseInt(br.readLine());
				numRows = Integer.parseInt(br.readLine());
				map = new int[numRows][numCols];
				width = numCols * tileSize;
				height = numRows * tileSize;
				
				xmin = GamePanel.WIDTH - width;
				xmax = 0;
				ymin = GamePanel.HEIGHT - height;
				ymax = 0;
				
				String delims = "\\t+";
				for(int row = 0; row < numRows; row++) {
					String line = br.readLine();
					String[] tokens = line.split(delims);
					for(int col = 0; col < numCols; col++) {
						map[row][col] = Integer.parseInt(tokens[col]);
					}
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
		public void setMapTile(int col, int row, int tile) {
			map[row][col] = tile;
		}
		
		public int getTileSize() { return tileSize;	}
		public double getX() { return x; }
		public double getY() { return y; }
		public int getWidth() { return width; }
		public int getHeight() { return height; }
		
		public void setTween(double t) {
			this.tween = t;
		}
		
		public int getType(int row, int col) {
			int rc = map[row][col];
			int r = rc / numTilesAcross;
			int c = rc % numTilesAcross;
			return tiles[r][c].getType();
			
		}
		
		public int getTile(int row, int col) {
			return map[row][col];
		}
		
		public void setPosition(double x, double y) {
			this.x += (x - this.x) * tween;
			this.y += (y - this.y) * tween;
			
			fixBounds();
			
			colOffset = (int) - this.x / tileSize;
			rowOffset = (int) - this.y / tileSize;
			
		}
		
		private void fixBounds() {
			
			// Player Edge-Position settings
			if(x < xmin) x = xmin;
			if(y < ymin) y = ymin;
			if(x > xmax) x = xmax;
			if(y > ymax) y = ymax;
		}
		
		public void fadeIn() {
			fadeIn = true;
			fadeOut = false;
			fadingAlpha = 0.0f;
			fadingColor = Color.black;
		}
		public void fadeOut() {
			fadeOut = true;
			fadeIn = false;
			fadingAlpha = 1.0f;
			fadingColor = Color.black;
		}
		public void finishFading() {
			fadeIn = false;
			fadeOut = false;
			fadingAlpha = 0.0f;
		}
		public void setFadeColor(Color c) { fadingColor = c; }
		
		public void setBackground(Background bg) { this.bg = bg; }
		public Background getBackground() { return this.bg; }
		
		public void setPlayer(Player p) { player = p; }
		public int getPlayerX() { return player.getX(); }
		public int getPlayerY() { return player.getY(); }
		
		public void setTextFont(Font textFont) { this.textFont = textFont; }
		public Font getTextfont() { return this.textFont; }
		
		public void setHearts(ArrayList<Love> hs) { 
			hearts = hs;
			if (hearts != null) {
				for (int i = 0; i < hearts.size(); i++) {
					hearts.get(i).setTileMap(this);
				}
				numHearts = hearts.size();
			}
		}
		public ArrayList<Love> getHearts() { return hearts; }
		public int getNumHearts() { return numHearts; }
		
		public void setCoins(ArrayList<Coin> cs) {
			coins = cs;
			if (coins != null) {
				for (int i = 0; i < coins.size(); i++) {
					coins.get(i).setTileMap(this);
				}
				numCoins = coins.size();
			}
		}
		public ArrayList<Coin> getCoins() { return coins; }
		public int getNumCoins() { return numCoins; }
		
		public void setGMNBoxes(ArrayList<GoldenMN> bxs) {
			gmnBoxes = bxs;
			if (gmnBoxes != null) {
				for (int i = 0; i < gmnBoxes.size(); i++) {
					gmnBoxes.get(i).setTileMap(this);
				}
			}
		}
		
		public void setActiveGMNBoxes(int n) { activeGMNBoxes = n; }
		public ArrayList<GoldenMN> getGMNBoxes() { return gmnBoxes; }
		public int getNumActiveGMNBoxes() { return activeGMNBoxes; }
		public void GMNBoxShouldCharge() {
			if (gmnBoxes != null) {
				for (int i = 0; i < gmnBoxes.size(); i++) {
					GoldenMN b = gmnBoxes.get(i);
					if (!b.notOnScreen()) b.charge();
					b.setPlayerPosition(player.getX(), player.getY());
				}
			}
		}
		
		public void setMusicBoxSets(ArrayList<MusicBoxSet> mbxs) { 
			musicBoxSets = mbxs;
			if (musicBoxSets != null) {
				for (int i = 0; i < musicBoxSets.size(); i++) {
					musicBoxSets.get(i).setTileMap(this);
				}
			}
		}
		public ArrayList<MusicBoxSet> getMusicBoxSets() { return musicBoxSets; }
		
		public void setLevelEndLove(LevelEndLove lel) {
			levelEndLove = lel;
			if (levelEndLove != null) levelEndLove.setTileMap(this);
		}
		public LevelEndLove getLevelEndLove() { return this.levelEndLove; }
		
		public void setHeartCage(HeartCage hc) {
			heartCage = hc;
			if (heartCage != null) heartCage.setTileMap(this);
		}
		public void setAltar(Image altar) { this.altar = altar; }
		
		public HeartCage getHeartCage() { return this.heartCage; }
		
		public void setSigns(ArrayList<Sign> sns) {
			signs = sns;
			if (signs != null) {
				for (int i = 0; i < signs.size(); i++) {
					signs.get(i).setTileMap(this);
				}
			}
		}
		public ArrayList<Sign> getSigns() { return this.signs; }
		public boolean isSignBeingRead() { return this.signIsBeingRead; }
		public void setSignIsBeingRead(boolean b) { signIsBeingRead = b; }
		
		public void setDoors(ArrayList<Door> drs) {
			doors = drs;
			if (doors != null) {
				for (int i = 0; i < doors.size(); i++) {
					doors.get(i).setTileMap(this);
				}
			}
		}
		public ArrayList<Door> getDoors() { return doors; }
		
		public void setEnemies(ArrayList<Enemy> nms) {
			enemies = nms;
			if (enemies != null) {
				for (int i = 0; i < enemies.size(); i++) {
					enemies.get(i).setTileMap(this);
				}
				numEnemies = enemies.size();
			}
		}
		public ArrayList<Enemy> getEnemies() { return this.enemies; }
		public int getNumEnemies() { return numEnemies; }
		
		public boolean levelEnd() { return this.levelEnd; }
		public boolean containsLevelEnd() { return containsLevelEnd; }
		
		public void update() {

			if (!containsLevelEnd || (containsLevelEnd && !heartCage.isDead())) {
				if (!isSignBeingRead()) {
					
					// set background
					if (bg != null) {
						bg.setPosition(getX(), getY());
						bg.update();
					}
				
					// update doors
					if (doors != null) {
						for (int i = 0; i < doors.size(); i++) {
							doors.get(i).update();
						} 
					}

					if (!fadeIn && !fadeOut) {
					// update all enemies
					// gmnBoxes
					if (gmnBoxes != null) {
						for (int i = 0; i < gmnBoxes.size(); i++) {
							GoldenMN b = gmnBoxes.get(i);
							b.update();
							if(b.isDead() && b.canSpawnHeart()){
								// add a heart
								Love h = new Love();
								h.setTileMap(this);
								if (hearts == null) hearts = new ArrayList<Love>();
								hearts.add(h);
								hearts.get(hearts.size()-1).setPosition(b.getX(), b.getY());
								hearts.get(hearts.size()-1).initSpawning();
								b.cantSpawnHeart();
								activeGMNBoxes--;
							}
						}
					}
					// other
					if (enemies != null) {
						for (int i = 0; i < enemies.size(); i++) {
							Enemy e = enemies.get(i);
							e.update();
							// remove them if they're dead
							if (e.isDead() && e.hasAnimationPlayedOnce() && !e.spawnedHeart()) {
								// add a heart
								Love h = new Love();
								h.setTileMap(this);
								if (hearts == null) hearts = new ArrayList<Love>();
								hearts.add(h);
								hearts.get(hearts.size()-1).setPosition(e.getX(), e.getY());
								e.didSpawnHeart();
							}
						if (e.shouldRemove()) enemies.remove(i);
						}
					}
					}
				
					// update coins
					if (coins != null) {
						for (int i = 0; i < coins.size(); i++) {
							coins.get(i).update();
						}
					}
					
					// update musicBoxSets
					if (musicBoxSets != null) {
						for (int i = 0; i < musicBoxSets.size(); i++) {
							musicBoxSets.get(i).update();
							if (musicBoxSets.get(i).getMusicBoxes() == null) musicBoxSets.remove(i);
						}
					}
					
					// update hearts
					if (hearts != null) {
						for (int i = 0; i < hearts.size(); i++) {
							hearts.get(i).update();
						}
					}
					
					// update signs
					if (signs != null) {
						for (int i = 0; i < signs.size(); i++) {
							signs.get(i).update();
						}
					}
				} else if (containsLevelEnd) {
					// update levelEnd stuff
					if (heartCage != null) heartCage.update();
					if (levelEndLove != null) levelEndLove.update();
					
					if (heartCage.isDead()) {
						if (!levelEndLove.levelEnd()) {
							bg.setPosition(getX(), getY());
						}
						levelEndLove.update();
						levelEnd = levelEndLove.levelEnd();
					}
				}
			}
		}
		
		public void draw(Graphics2D g) {

			if (!levelEnd()) {
				
				// draw bg
				bg.draw(g);
				
				// draw doors
				if (doors != null) {
					for (int i = 0; i < doors.size(); i++) {
						doors.get(i).draw(g);
					}
				}
				
				// draw coins;
				if (coins != null) {
					for (int i = 0; i < coins.size(); i++) {
						coins.get(i).draw(g);
					}
				}
				
				// draw hearts not collected
				if (hearts != null) {
					for (int i = 0; i < hearts.size(); i++) {
						if (!hearts.get(i).isCollected()) hearts.get(i).draw(g);
					}
				}
				
				// draw enemies
				if (gmnBoxes != null) {
					for (int i = 0; i < gmnBoxes.size(); i++) {
						gmnBoxes.get(i).draw(g);
					}
				}
				// draw musicBoxSets
				if (musicBoxSets != null) {
					for (int i = 0; i < musicBoxSets.size(); i++) {
						musicBoxSets.get(i).draw(g);
					}
				}
				// other
				if (enemies != null) {
					for (int i = 0; i < enemies.size(); i++) {
						enemies.get(i).draw(g);
					}
				}
				
				// draw signs when not being read in background
				if (signs != null) {
					for (int i = 0; i < signs.size(); i++) {
						if (!signs.get(i).isBeingRead()) signs.get(i).draw(g);
					}
				}
				
				// draw levelEnd components
				if (containsLevelEnd) {
					g.drawImage(altar,(int) (heartCage.getX() + getX())-30, (int) (heartCage.getY() + getY()) - 10, null);
					levelEndLove.draw(g);
					heartCage.draw(g);
				}
				
				// draw Player behind tileMap when alive
				if (player != null) if(!player.isDead()) player.draw(g);
				
				// draw myself
				for(int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
				
					if(row >= numRows) break;
				
					for(int col = colOffset; col < colOffset + numColsToDraw; col++) {
					
						if(col >= numCols) break;
					
						if(map[row][col] == 0) continue;
					
						int rc = map[row][col];
						int r = rc / numTilesAcross;
						int c = rc % numTilesAcross;
					
						g.drawImage(tiles[r][c].getImage(), (int) x + col * tileSize, (int) y + row * tileSize, null);
					
					}
				
				}
				
				// draw Player in front of tileMap when dead
				if (player != null) if(player.isDead()) player.draw(g);
				
				if (isSignBeingRead()) {
					// dark everything else out
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.67f));
					g.setColor(Color.black);
					g.fillRect(0, 20, GamePanel.WIDTH, GamePanel.HEIGHT);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
					// draw Signs when being read in foreground
					if (signs != null) {
						for (int i = 0; i < signs.size(); i++) {
							if (signs.get(i).isBeingRead()) signs.get(i).draw(g);
						}
					}
				}
				
				// draw hearts collected
				if (hearts != null) {
					for (int i = 0; i < hearts.size(); i++) {
						if (hearts.get(i).isCollected()) hearts.get(i).draw(g);
					}
				}
				
				// draw levelEndLove collected
				if (containsLevelEnd) {
					if (levelEndLove.isCollected()) levelEndLove.draw(g);
				}
				
				if (fadeIn || fadeOut) {
					if (fadeIn) {
						if (fadingAlpha <= 1.0f) {
							fadingAlpha += 0.03;
						} if (fadingAlpha > 1.0) {
							fadingAlpha = 1.0f;
						}
					} else {
						if (fadingAlpha >= 0.0f) {
							fadingAlpha -= 0.021;
							
						} if (fadingAlpha < 0.0){
							fadingAlpha = 0.0f;
							finishFading();
						}
					}
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadingAlpha));
					g.setColor(fadingColor);
					g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
				}	
				
			}
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		}

}
