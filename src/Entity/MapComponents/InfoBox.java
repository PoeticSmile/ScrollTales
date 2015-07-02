package Entity.MapComponents;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class InfoBox {
	
	private BufferedImage top;
	private BufferedImage middle;
	private BufferedImage bottom;
	private BufferedImage enterButton;
	
	private long elapsed, flinchTimer;
	
	protected int width = 220;
	protected int tbHeight = 10;
	protected int mHeight = 16;
	
	private int x = 50;
	private int y = 30;
	
	private String text[];
	private int lines;
	private double maxChars = 42; //originally 18
	Font textFont;
	
	private boolean isDisplaying;
	private boolean READ;
	
	public InfoBox(String text, boolean useTextFontNormal, Font font) {
		
		
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/HUD/InfoBox.gif"));
			top = spritesheet.getSubimage(0, 0, width, tbHeight);
			middle = spritesheet.getSubimage(0, tbHeight, width, mHeight);
			bottom = spritesheet.getSubimage(0, tbHeight + mHeight, width, tbHeight);
			enterButton = ImageIO.read(getClass().getResourceAsStream("/HUD/EnterButton.gif"));
			
			fillInfoBox(text);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if (useTextFontNormal) textFont = new Font("Dialog", Font.BOLD, 9);
		else textFont = font;
		
		isDisplaying = false;
		READ = false;
	}
	
	public void fillInfoBox(String infos) {
		
		lines = (int) (infos.length() / maxChars);
		if((infos.length()/maxChars) % 2 != 0) lines++;
		text = new String[lines];
		int length = 0;
		
		text[0] = infos.substring(0, infos.lastIndexOf(" ", (int) maxChars));
		length += text[0].length();
		for(int i = 1; i < lines; i++) {
			text[i] = infos.substring(length, infos.lastIndexOf(' ', length + (int) maxChars));
			length += text[i].length();
		}

		flinchTimer = System.nanoTime();
		
	}
	
	public void update() {}
	
	public void draw(Graphics2D g) {
		
		if (isDisplaying()) {

			g.setFont(textFont);
			g.setColor(new Color(79, 19, 0));
		
			if(text != null) {
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
					g.drawString(text[i], x+5, y);
				}
		
				//draw PRESS ENTER
				elapsed = (System.nanoTime() - flinchTimer) / 1000000;
				if(elapsed / 1000 %2 != 0) {
					g.drawImage(enterButton, x + 120, y, null);
				}
		
				x = 70;
				y = 30;
			}
		}
		
	}
	
	public void enterKeyWasReleased() {
		if (!isDisplaying()) {
			isDisplaying = true;
			if (!READ) READ = true;
		} else {
			isDisplaying = false;
		}
		
	}

	public boolean isDisplaying() { return isDisplaying; }
	public boolean isRead() { return READ; }
	
	public void actionPerformed(ActionEvent e) {}
	

}
