package Entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class HUD {
	
	private Player player;
	private BufferedImage good;
	private BufferedImage bad;
	private BufferedImage dead;
	
	private BufferedImage coin;
	private int coinsFound;
	
	private int width = 24;
	private int height = 22;
	
	Font titleFont;
	Font infoFont;
	
	
	
	public HUD(Player p) {
		player = p;
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/HUD/heartContainer.gif"));
			good = spritesheet.getSubimage(0, 0, width, height);
			bad = spritesheet.getSubimage(width, 0, width, height);
			dead = spritesheet.getSubimage(2 * width, 0, width, height);
			coin = ImageIO.read(getClass().getResource("/HUD/Coin.gif"));
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		titleFont = new Font("Arial", Font.PLAIN, 16);
		infoFont = new Font("Arial", Font.PLAIN, 11);
	}
	
	public void draw(Graphics2D g) {
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.4));
		g.setColor(Color.white);
		g.fillRect(0, 0, GamePanel.WIDTH * GamePanel.SCALE -1, 20);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
		g.setFont(titleFont);
		g.drawString("Center", 134, 16);
		
		g.setFont(infoFont);
		g.drawString("Love: " + player.getNumHearts(), 210, 14);
		
		g.setFont(infoFont);
		g.drawString("Hate: " + player.getNumEnemies(), 275, 14);

		
		for(int i = 0; i < 3; i++) {
			if(coinsFound > i) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));				
			} else {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
			}
			g.drawImage(coin, 40 + 20*i, 2, null);
		}
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		
		
		switch(player.getHealth()) {
		
		case 2 :	g.drawImage(good, 5, 25, null);
					break;
		case 1 :	g.drawImage(bad, 5, 25, null);
					break;
		case 0 :	g.drawImage(dead, 5, 25, null);
		
		
		}
		
	}
	
	public void coinsFound(int c) {
		coinsFound = c;
	}

}
