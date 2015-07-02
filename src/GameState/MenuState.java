package GameState;

import Audio.AudioPlayer;
import Entity.Awesome;
import Entity.Rainbow;
import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MenuState extends GameState {
	
	private Background bg;
	
	private int currentChoice = 0;	
	private String[] options = {
			"Start",
			"Quit"
	};
	
	private Color titleColor;
	private Color selectionColor;
	private Font titleFont;
	private Font font;
	private Awesome awesome;
	private ArrayList<Rainbow> rainbows;
	
	private double ff = 1;
	private boolean ffIn;
	
	private AudioPlayer select;
	private AudioPlayer selected;
	
	
	public MenuState(GameStateManager gsm) {
		
		this.gsm = gsm;
		
		try {
			
			bg = new Background("/Backgrounds/menuFileIII.gif", 1, true);
			bg.setVector(-0.7, 0);
			
			titleColor = new Color(128, 0, 0);
			selectionColor = new Color(17, 117, 180);
			titleFont  = new Font("Serif", Font.ITALIC, 28);
			font = new Font("Arial", Font.PLAIN, 10);
			
			awesome = new Awesome();
			awesome.setPosition(-80, 80);
			awesome.setVector(3, 0);
			rainbows = new ArrayList<Rainbow>();
			
			select = new AudioPlayer("/SFX/select.wav");
			selected = new AudioPlayer("/SFX/selected.wav");
			
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void init() {}
	
	public void update() {
		bg.update();

	}
	
	public void draw(Graphics2D g) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
		// draw bg
		bg.draw(g);
		
		// draw Awesome Smiley
		awesome.update();
		rainbows.add(new Rainbow(awesome.getX(), awesome.getY()));

		for(int i = 0; i < rainbows.size(); i++) {
			rainbows.get(i).update();
			if(rainbows.get(i).shouldRemove()) rainbows.remove(i);
			else rainbows.get(i).draw(g);
			
		}
		awesome.draw(g);
		
		// draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("Tales of Awesomeness", 40, 60);
		
		// draw menu options
		for(int i = 0; i < options.length; i++) {
			if(i == currentChoice) {
				g.setColor(selectionColor);
			}
			else {
				g.setColor(Color.DARK_GRAY);
			}
			g.drawString(options[i], 140, 140 + i * 20);
		}
		
		if (ff < 1 && ffIn) {
			ff += 0.001;
		} else {
			ffIn = false;
		}
		if (ff > 0 && !ffIn) {
			ff -= 0.001;
			if (ff < 0) {
				ffIn = true;
				ff = 0;
			}
		}
		
		g.setColor(Color.BLACK);
		if (ff < 1) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1 - ff)));
		} else {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0));
		}
		g.setFont(font);
		g.drawString("Programmed and designed by Bruno Stucki", 5, 275);
		g.drawString("Alpha Version 0.4.3", 300, 275);
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1));
		
		
	}
	
	
	private void select() {
		selected.play();
		if(currentChoice == 0) {
			gsm.setState(GameStateManager.WORLDSELECTSTATE);
		}
		
		if(currentChoice == 1) {
			System.exit(0);
		}
		
	}
	
	public void stop() {}
	
	public void resume() {}
	
	public void playSound(AudioPlayer s) {
		
		s.play();
	}
	
	public void keyPressed(int k) {
	
		
		switch(k) {
		
		case KeyEvent.VK_UP :		currentChoice--;	if(currentChoice == -1) currentChoice = options.length - 1; select.play();
									break;
		case KeyEvent.VK_DOWN :		currentChoice++;	if(currentChoice == options.length) currentChoice = 0; select.play();
					
		}
	}
	
	
	public void keyReleased(int k) {
		
		if(k == KeyEvent.VK_ENTER)	select();
		
		if(k == KeyEvent.VK_M) {
			
		}
	
	}
	
	
	
	
	
	
	
	
	

}
