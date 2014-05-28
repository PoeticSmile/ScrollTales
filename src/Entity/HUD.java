package Entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import GameState.GameStateManager;
import Main.GamePanel;

public class HUD {
	
	private Player player;
	private BufferedImage good;
	private BufferedImage bad;
	private BufferedImage dead;
	private BufferedImage mute;
	
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
			
			mute = ImageIO.read(getClass().getResource("/HUD/Mute.gif"));
			
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
		g.drawString("Center", 178, 16);
		
		g.setFont(infoFont);
		g.drawString("Coins left: " + player.getNumCoins(), 80, 14);
		
		g.setFont(infoFont);
		g.drawString("Enemies left: " + player.getNumEnemies(), 275, 14);
		
		/*if(GameStateManager.MUTE) {
			g.drawImage(mute, 360, 6, null);
		}*/
				
		
		
		
		switch(player.getHealth()) {
		
		case 2 :	g.drawImage(good, 376, -1, 24, 22, null);
					break;
		case 1 :	g.drawImage(bad, 376, -1, 24, 22, null);
					break;
		case 0 :	g.drawImage(dead, 376, -1, 24, 22, null);
		
		
		}
		
	}

}
