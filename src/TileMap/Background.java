package TileMap;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;

import Main.GamePanel;


public class Background {
	
	private BufferedImage image;
	
	private double x;
	private double y;
	private double dx;
	private double dy;
	
	int fssw;
	int fssh;
	boolean menubg;
	private double moveScale;
	
	public Background(String s, double ms, boolean menubg) {
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(s));
			moveScale = ms;
			if(menubg) {
				fssw = GamePanel.WIDTH / image.getWidth();
				fssh = GamePanel.HEIGHT / image.getHeight();
			} else {
				fssw = fssh = 1;
			}
			this.menubg = menubg;
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setPosition(double x, double y) {
		this.x = (x * moveScale) % GamePanel.WIDTH;
		this.y = (y * moveScale) % GamePanel.HEIGHT;
	}

	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void update() {
		x += dx;
		y += dy;
	}
	
	public void draw(Graphics2D g) {
		
		if(x <= -image.getWidth()*fssw) {
			x = 0;
		}
		
		g.drawImage(image, (int) x, (int) y, image.getWidth() * fssw, image.getHeight() * fssh, null);
		
		if(menubg) {
		if(x < 0) {
			g.drawImage(image,  (int)x + GamePanel.WIDTH, (int)y, image.getWidth() * fssw, image.getHeight() * fssh, null);
		}
		if (x > 0) {
			g.drawImage(image, (int)x - GamePanel.WIDTH, (int)y, image.getWidth() * fssw, image.getHeight() * fssh, null);
		}
		}
	}
	

}
