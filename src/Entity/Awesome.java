package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Awesome {
	
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {8};
	
	private int width = 40;
	private int height = 40;
	
	private static final int TURNING = 0;
	
	private Animation animation;
	private int currentAction = 0;
	
	
	// movement
	private double x, y;
	private double dx = 3;
	private double alpha;
	
	
	public Awesome() {
		
		
		
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/awesomeTurning40px.gif"));
			sprites = new ArrayList<BufferedImage[]>();
			
				BufferedImage[] bi = new BufferedImage[numFrames[0]];
				
				for(int j = 0; j < numFrames[0]; j++) {
					
					bi[j] = spritesheet.getSubimage(j*width, 0, width, height);
					
				}
				sprites.add(bi);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = TURNING;
		animation.setFrames(sprites.get(TURNING));
		animation.setDelay(80);
		
		
	}
	
	public void setAction(int a) {
		if(currentAction != a) {
			currentAction = a;
			animation.setFrames(sprites.get(a));
		}
	}
	
	public void setDelay(int d) {
		animation.setDelay(d);
	}
	
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setVector(double dx, double alpha) {
		this.dx = dx;
		this.alpha = alpha;
	}
	
	public int getX() { return (int) x; }
	public int getY() { return (int) y; }
	public int getDelay() { return animation.getDelay(); }
	public int getNumPlays() { return animation.getNumPlays(); }
	
	
	public void update() {
		
		
			
			
			
			alpha += 0.04;
			
			x += dx;
			y += 1.7*Math.sin(alpha);
			if(x > 520) x = -80;
			
			if(y > 300) y = 20;
		
		
		
		
		
		
		animation.update();
	}
	
	public void draw(Graphics2D g) {
		
		g.drawImage(animation.getImage(),(int) x,(int) y, null);
		
	}

}
