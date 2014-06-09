package Entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Rainbow {
	
	
	private BufferedImage rainbow;
	
	private boolean remove;

	private int x, y;
	private double f;
	
	private int counter = 0;
	
	public Rainbow(int x, int y) {
		
		this.x = x;
		this.y = y;
		
		
		try {
			
			rainbow = ImageIO.read(getClass().getResource("/rainbow.gif"));
			
			f = 1;
			
				
			
		}catch(Exception e) {
			e.printStackTrace();
		}

		
		
		
		
	}

	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() { return (int) x; }
	public int getY() { return (int) y; }
	public boolean shouldRemove() { return remove; }
	
	
	public void update() {
		
		counter++;
		
		if(counter >50) {
			f -= 0.025;
			if(f < 0) remove = true;
		}
		
	}
	
	public void draw(Graphics2D g) {
		
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) f));
			g.drawImage(rainbow, x, y, null);
		
	}

}
