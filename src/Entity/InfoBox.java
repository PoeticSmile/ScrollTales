package Entity;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class InfoBox {
	
	private BufferedImage top;
	private BufferedImage middle;
	private BufferedImage bottom;
	
	protected int width = 120;
	protected int tbHeight = 5;
	protected int mHeight = 8;
	
	private int x = 280;
	private int y = 30;
	
	private String info[];
	private int lines;
	
	Font infoFont;
	
	
	
	public InfoBox() {
		
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/HUD/InfoBox.gif"));
			top = spritesheet.getSubimage(0, 0, width, tbHeight);
			middle = spritesheet.getSubimage(0, tbHeight, width, mHeight);
			bottom = spritesheet.getSubimage(0, tbHeight + mHeight, width, tbHeight);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		infoFont = new Font("Arial", Font.PLAIN, 9);
	}
	
	public void fillInfoBox(String infos, int lines) {
		this.lines = lines;
		info = new String[lines];
		int length = 0;
		
		for(int i = 0; i < lines; i++) {
			info[i] = infos.substring(length, infos.indexOf('-', length));
			length += info[i].length();
			System.out.println(length);
		}
		
		
		
		
	}
	
	public void update() {
		
	}
	
	public void draw(Graphics2D g) {
		g.setFont(infoFont);
		g.setColor(Color.BLACK);
		g.drawImage(top, x, y, null);
		y += 5;
		for(int i = 0; i < lines; i++) {
			g.drawImage(middle, x, y, null);
			y += 8;
		}
		g.drawImage(bottom, x, y, null);
		y = 34;
		for(int i = 0; i < lines; i++) {
			y += 8;
			g.drawString(info[i], x+7, y);
		}
		x = 280;
		y = 30;
		
	}

}
