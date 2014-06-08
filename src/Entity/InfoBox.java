package Entity;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class InfoBox implements ActionListener {
	
	private BufferedImage top;
	private BufferedImage middle;
	private BufferedImage bottom;
	
	private long elapsed, flinchTimer;
	
	protected int width = 120;
	protected int tbHeight = 5;
	protected int mHeight = 8;
	
	private int x = 280;
	private int y = 30;
	
	private String info[];
	private int lines;
	private double maxChars = 20; //originally 18
	
	
	
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
		
		infoFont = new Font("Dialog", Font.BOLD, 9);
	}
	
	public void fillInfoBox(String infos) {
		
		lines = (int) (infos.length() / maxChars);
		if((infos.length()/maxChars) % 2 != 0) lines++;
		info = new String[lines];
		int length = 0;
		System.out.println((infos.length() / maxChars)%2);
		
		info[0] = infos.substring(0, infos.lastIndexOf(" ", (int) maxChars));
		length += info[0].length();
		for(int i = 1; i < lines; i++) {
			info[i] = infos.substring(length, infos.lastIndexOf(' ', length + (int) maxChars));
			length += info[i].length();
		}

		flinchTimer = System.nanoTime();
		
	}
	
	public void update() {

		
		
	}
	
	public void draw(Graphics2D g) {
		g.setFont(infoFont);
		g.setColor(new Color(79, 19, 0));
		
		if(info!=null) {
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
			g.drawString(info[i], x+5, y);
		}
		
		//draw PRESS ENTER
		elapsed = (System.nanoTime() - flinchTimer) / 1000000;
		if(elapsed / 1000 %2 != 0) {
			g.setColor(Color.white);
			g.drawString("Press ENTER", x + 10, y + 20);
		}
		
		
		x = 280;
		y = 30;
		}
		
		
		
	}

	public void enterWasPressed() {
		info = null;
	}
	public boolean isDisplayed() { return (info != null); }
	public void actionPerformed(ActionEvent e) {

		
		
	}

}
