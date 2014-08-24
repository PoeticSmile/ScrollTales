package GameState;

import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

import Audio.AudioPlayer;
import Main.GamePanel;
import TileMap.Background;

public class WorldSelectState extends GameState {
	
	private Background bg;
	Image currentSelection;
	
	private int currentChoice = 6;
	private String[] worlds = {
			"SPACE",
			"CLOUDS",
			"BALLON",
			"OVERWORLD",
			"BUNKER",
			"CAVE",
			"CENTER"
	};
	
	private Color fontColor;
	private Font titleFont;
	
	private AudioPlayer select;
	private AudioPlayer selected;
	
	public static int[] unlockedWorlds = new int[7];
	
	
	
	
	public WorldSelectState(GameStateManager gsm) {
		this.gsm = gsm;
		
		try {
			
			bg = new Background("/Backgrounds/WorldSlct.gif", 0, true);
			bg.setVector(0, 0);
			ImageIcon cs = new ImageIcon(this.getClass().getResource("/Backgrounds/currentSelection.png"));
			currentSelection = cs.getImage();
			
			fontColor = new Color(0, 0, 0);
			titleFont = new Font("Arial", Font.BOLD, 12);
			
			select = new AudioPlayer("/SFX/select.wav");
			selected = new AudioPlayer("/SFX/selected.wav");
			
			GamePanel.loadWorlds();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	public void init() {}

	
	public void update() {
		bg.update();
	}

	public void draw(Graphics2D g) {
	
		// draw bg
		bg.draw(g);
		
	
		
		
		
		// draw worldtitles
		g.setColor(fontColor);
		g.setFont(titleFont);
		for(int i = 0; i < worlds.length; i++) {
			if(i == currentChoice) {
				g.drawImage(currentSelection, 160, 5 + i * 40, null);
				g.setColor(Color.green);
			} else {
				g.setColor(Color.white);
			}
			g.drawString(worlds[i], 170, 20 + i * 40);
		}
		
		// cover unachieved worlds
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		g.setColor(Color.DARK_GRAY);
		
		for(int i = 0; i < unlockedWorlds.length; i++) {
			if(unlockedWorlds[i] == 0) {
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0, i * 40, 400, 40);
				g.setColor(Color.red);
				g.drawRect(0, i * 40, GamePanel.WIDTH - 1, 39);
				g.drawString("Level not unlocked yet", 160, 20 + i * 40);
				
			} else if(unlockedWorlds[i] == 1) {
				g.setColor(Color.GREEN);
				g.drawRect(0, i * 40, GamePanel.WIDTH - 1, 39);
			}
		}
		
	
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
	}
	
	public void select() {
		selected.play();
		switch(currentChoice) {
		
		
		case 6 :	gsm.setState(GameStateManager.LSCENTER);
					break;
		case 5 :	gsm.setState(GameStateManager.CAVESTATE);
					break;
		case 4 :	gsm.setState(GameStateManager.BUNKERSTATE);
					break;
		case 3 :	gsm.setState(GameStateManager.OVERWORLDSTATE);
					break;
		case 2 :	gsm.setState(GameStateManager.BALLONSTATE);
					break;
		case 1 :	gsm.setState(GameStateManager.CLOUDSSTATE);
					break;
		case 0 :	gsm.setState(GameStateManager.SPACESTATE);
					break;
		}
		
		
	}
	
	public void stop() {}
	
	public void resume() {}
	
	public void playSound(AudioPlayer s) {
		
	}

	public void keyPressed(int k) {
		
		if(k == KeyEvent.VK_UP) {
			if(currentChoice > 0 && unlockedWorlds[currentChoice - 1] == 1) {
				select.play();
				currentChoice--;
			}
			
		}
		
		if(k == KeyEvent.VK_DOWN) {
			if(currentChoice < 6) {
				select.play();
				currentChoice++;
			}
		}
		
		
		
		
		
		
	}

	public void keyReleased(int k) {
		
		if(k == KeyEvent.VK_ENTER) {
			select();
		}
		
		if(k == KeyEvent.VK_ESCAPE)	{
			selected.play();
			gsm.setState(GameStateManager.MENUSTATE);
		}
		
		if(k == KeyEvent.VK_M) {
			
		}
		
	}

}
