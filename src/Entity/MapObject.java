package Entity;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Main.GamePanel;
import TileMap.TileMap;

public abstract class MapObject {
	
	// tile stuff
	protected TileMap tileMap;
	protected int tileSize;
	protected double xmap;
	protected double ymap;
	
	// position and vector
	protected double x;
	protected double y;
	protected double dx;
	protected double dy;
	
	// dimensions
	protected int width;
	protected int height;
	
	// collision box
	protected int cwidth;
	protected int cheight;
	
	// collision
	protected int currRow;
	protected int currCol;
	protected double xdest;
	protected double ydest;
	protected double xtemp;
	protected double ytemp;
	protected int topLeft;
	protected int topRight;
	protected int bottomLeft;
	protected int bottomRight;
	
	// animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingLeft;
	protected float fadingOut = 1;
	
	// movement
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;
	protected boolean charging;
	
	// movement attributes
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpStart;
	protected double stopJumpSpeed;
	
	// constructor
	public MapObject(TileMap tm) {
		tileMap = tm;
		tileSize = tm.getTileSize();
	}
	
	public boolean intersects(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();
		return r1.intersects(r2);
	}
	
	public Rectangle getRectangle() {
			
		return new Rectangle((int) x - cwidth/2, (int) y - cheight/2, cwidth, cheight);
		
	}
	
	public void calculateCorners(double x, double y) {
		
		int leftTile = (int) (x - cwidth / 2) / tileSize;
		int rightTile = (int) (x + cwidth / 2 -1) / tileSize;
		int topTile = (int) (y - cheight / 2) / tileSize;
		int bottomTile = (int) (y + cheight / 2 - 1) / tileSize;
		
		int tl = tileMap.getType(topTile, leftTile);
		int tr = tileMap.getType(topTile, rightTile);
		int bl = tileMap.getType(bottomTile, leftTile);
		int br = tileMap.getType(bottomTile, rightTile);
		
		
		topLeft = tl;
		topRight = tr;
		bottomLeft = bl;
		bottomRight = br;
	}
	
	public int getTopLeft() { return topLeft; }
	public int getTopRight() { return topRight; }
	public int getBottomLeft() { return bottomLeft; }
	public int getBottomRight() { return bottomRight; }
	
	public void checkTileMapCollision() {
		
		/********************************************************************************************
		 *  NORMAL = 0;																				*
		 *	BLOCKED  = 1;																			*
		 *	JUMPTHROUGH = 2;																		*
		 *	EVENT = 3;																				*
		 ********************************************************************************************/
		
		
		currCol = (int) x / tileSize;
		currRow = (int) y / tileSize;
		
		xdest = x + dx;
		ydest = y + dy;
		
		xtemp = x;
		ytemp = y;
		calculateCorners(x, ydest);
		if(dy < 0) {							// because Love is out of this world
			if((topLeft == 1 || topRight == 1) && !this.getClass().getName().equals("Entity.Love")) {
				dy = 0;
				ytemp = currRow * tileSize + cheight / 2;
			}
			else {
				ytemp += dy;
			}
		}
		if(dy > 0) {
			if((bottomLeft == 1 || bottomRight == 1) || (bottomLeft == 2 || bottomRight == 2)) {
				
					dy = 0;
					falling = false;
					ytemp = (currRow + 1) * tileSize - cheight / 2;
				
			}
			else {
				ytemp += dy;
			}
		}
		
		if(dy == 0 && (this.getClass().getName().equals("Entity.Player") && (topLeft == 2 || topRight == 2))) {
			dy = 2;
		}
		
		calculateCorners(xdest, y);
		if(dx < 0) {
			if(topLeft == 1 || bottomLeft == 1) {
				dx = 0;
				xtemp = currCol * tileSize + cwidth / 2;

				if(this.getClass().getName().equals("Entity.MusicNote")) xtemp = currCol * tileSize + cwidth/2 -1;
			}
			else {
				xtemp += dx;
			}
		}
		if(dx > 0) {
			if(topRight == 1 || bottomRight == 1) {
				dx = 0;
				xtemp = (currCol + 1) * tileSize - cwidth / 2;
				if(this.getClass().getName().equals("Entity.MusicNote")) xtemp = currCol * tileSize + (tileSize-cwidth/2) +1;

			}
			else {
				xtemp += dx;
			}
		}
		
		if(!falling) {
			calculateCorners(x, ydest + 1);
			if(bottomLeft != 1 && 1 != bottomRight && bottomLeft != 2 && bottomRight != 2) {
				falling = true;
			}
		}
		
		// badMusicNote hitting
		if(this.getClass().getName().equals("Entity.Enemies.BadMusicNote")) {
			if(bottomLeft == 1 || bottomRight == 1 || topLeft == 1 || topRight == 1) {
				dx = 0;
				moveSpeed = fallSpeed = 0;
				dy = 0;
			}
		}
		
	}
	
	public void moveWithoutCollisionDetection() {
		xtemp = x;
		ytemp = y;
		xtemp += dx;
		ytemp += dy;
	}
	
	public int getX() { return (int) x; }
	public int getY() { return (int) y; }
	public int getXMap() { return (int) xmap; }
	public int getYMap() { return (int) ymap; }
	public double getDx() { return dx; }
	public double getDy() { return dy; }
	public boolean getFacingLeft() { return facingLeft; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getCWidth() { return cwidth; }
	public int getCHeight() { return cheight; }
	public int getCurrentAction() { return currentAction; }
	
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setTemp(double xtemp, double ytemp) {
		this.xtemp = xtemp;
		this.ytemp = ytemp;
	}
	public void setvector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void setMapPosition() {
		xmap = tileMap.getX();
		ymap = tileMap.getY();
	}
	
	public void setLeft(boolean b) { left = b; }
	public void setRight(boolean b) { right = b; }
	public void setUp(boolean b) { up = b; }
	public void setDown(boolean b) { down = b; }
	public void setJumping(boolean b) { jumping = b; }

	public boolean notOnScreen() {
		return x + xmap + width < 0 ||
				x + xmap - width > GamePanel.WIDTH ||
				y + ymap + height < 0 ||
				y + ymap - height > GamePanel.HEIGHT;
	}
	
	public void draw(Graphics2D g) {
		
		// Love despawning
		if(this.getClass().getName().equals("Entity.Love") && width != cwidth && height != cheight) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		}
		
		if(facingLeft) {
			
			g.drawImage(animation.getImage(), (int) (x + xmap - width / 2), (int) (y + ymap - height / 2), width, height, null);

		}
		else {

			g.drawImage(animation.getImage(), (int) (x + xmap - width / 2 + width), (int) (y + ymap - height / 2), -width, height, null);
			
		}
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		
	}

}
