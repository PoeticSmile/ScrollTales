package TileMap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import Main.GamePanel;


	public class TileMap {
	
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
		
		public TileMap(int tileSize) {
			this.tileSize = tileSize;
			numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
			numColsToDraw = GamePanel.WIDTH / tileSize + 22;
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
		
		
		public void draw(Graphics2D g) {
			
			for(int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
				
				if(row >= numRows) break;
				
				
				for(int col = colOffset; col < colOffset + numColsToDraw; col++) {
					
					if(col >= numCols) break;
					
					if(map[row][col] == 0) continue;
					
					int rc = map[row][col];
					int r = rc / numTilesAcross;
					int c = rc % numTilesAcross;
					
					g.drawImage(tiles[r][c].getImage(), (int) x + col * tileSize, (int) y + row * tileSize, null);
					
					/*for(int xp = 0; xp < tileSize; xp++) {
						for(int yp = 0; yp < tileSize; yp++) {
							if(!tiles[r][c].getImage().getSubimage(xp, yp, 1, 1).isAlphaPremultiplied()) {
								g.drawImage(tiles[r][c].getImage().getSubimage(xp, yp, 1, 1), (int) (xp + x + col * tileSize), (int) (yp + y + row * tileSize), null);
							}
						}
					}*/
				}
				
			}
		}

}
