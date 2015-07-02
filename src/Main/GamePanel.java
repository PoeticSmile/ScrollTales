package Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JPanel;

import Audio.AudioPlayer;
import Entity.Awesome;
import Entity.Rainbow;
import GameState.GameStateManager;
import GameState.WorldSelectState;
import LevelSelections.LSCenter;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener{
	
	
	
	// dimensions
	public static final int WIDTH = 320; //400
	public static final int HEIGHT = 240;//280
	public static final int SCALE = 2;
	
	// game thread
	private Thread thread;
	private boolean running;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;
	
	// image
	private BufferedImage image;
	public static Graphics2D g;
	private int pixelate = 0;
	private int manipulate = 0;
	
	// fading & pause stuff
	boolean fIn = true;
	boolean cIn = true;
	int col;
	double f = 0;
	double fi = 0;
	double fo = 1;
	private int currentChoice = 0;
	private String[] options = {
			"Resume",
			"Back to Levelselection"
	};
	
	private Awesome awesome;
	private ArrayList<Rainbow> rainbows;
	private HashMap <String, AudioPlayer> songs;
	private HashMap <String, AudioPlayer> sfx;
	private Font titleFont;
	
	// game state manager
	private GameStateManager gsm;
	
	// saves
	static Properties properties;
	static FileInputStream in;
	static FileOutputStream out;
	
	
	
	public GamePanel() {
		super();
		
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
		
		
	}
	
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	
	private void init() {
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		
		try {
			

			properties = new Properties();
			in = new FileInputStream("Resources/Saves/Properties.properties");
			properties.load(in);
			in.close();
			
			awesome = new Awesome();
			awesome.setPosition(-80, 100);
			awesome.setVector(3, 0);
			rainbows = new ArrayList<Rainbow>();
			
			songs = new HashMap <String, AudioPlayer>();
			//songs.put("recovery", new AudioPlayer("/Music/Recovery_CoA.mp3"));
			
			sfx = new HashMap <String, AudioPlayer>();
			sfx.put("select", new AudioPlayer("/SFX/select.wav"));
			sfx.put("selected", new AudioPlayer("/SFX/selected.wav"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		titleFont = new Font("Arial", Font.PLAIN, 16);
		
		running = true;
		
		gsm = new GameStateManager();
		//if(gsm.getStateToLoad() < 2) recovery.play();
	}
	
	public void run() {
		
		init();
		
		long start;
		long elapsed;
		long wait;
		
		// game loop
		while(running) {
			
			start = System.nanoTime();
			
			if(!gsm.isGamePaused() && !GameStateManager.loadingScreen) {
				update();
			}
			draw();
			drawToScreen();
			
			elapsed = System.nanoTime() - start;
			
			wait = targetTime - elapsed / 1000000;
			if(wait < 0) wait = 5;
			
			try {
				Thread.sleep(wait);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	private void update() {
		
		gsm.update();
		//if(gsm.getCurrentState() > 1) recovery.stop();
	}
	
	private void draw() {
	gsm.draw(g);
	
	// HUD
	

	// GamePaused
	if(gsm.isGamePaused()) {
				
		if(fIn) {
			if(f + 0.01 < 1) {
				g.setColor(new Color(col, col, col));
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) f));
				g.fillRect(0, 20, GamePanel.WIDTH * SCALE, GamePanel.HEIGHT * SCALE - 20);
				f += 0.01;
				if(cIn) {
					if(col < 254) {
						col += 1;
					} else {
						cIn = false;
					}
				} else if(col > 2) {
					col -= 1;
				} else {
					cIn = true;
				}
			} else {
				fIn = false;
			}
		}

		if(!fIn) {
			if(f - 0.01 > 0) {
				g.setColor(new Color(col, col, col));
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) f));
				g.fillRect(0, 20, GamePanel.WIDTH * SCALE, GamePanel.HEIGHT * SCALE - 20);
				f -= 0.01;
				if(cIn) {
					if(col < 254) {
						col += 1;
					} else {
						cIn = false;
					}
				} else if(col > 2) {
					col -= 1;
				} else {
					cIn = true;
				}
			} else {
				fIn = true;
			}
		}
		g.setColor(Color.white);
		g.setFont(new Font("Serif", Font.ITALIC, 17));

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
		//Awesome Smiley
		awesome.update();
		rainbows.add(new Rainbow(awesome.getX(), awesome.getY()));

		for(int i = 0; i < rainbows.size(); i++) {
			rainbows.get(i).update();
			if(rainbows.get(i).shouldRemove()) rainbows.remove(i);
			else rainbows.get(i).draw(g);
			
		}
		awesome.draw(g);
		
		//MenuPanel
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
		g.setColor(Color.white);
		g.fillRect(100, 70, 200, 90);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		g.drawRect(100, 70, 200, 90);
		
		// draw GamePausedMenu
		if(gsm.isGamePaused()) {
			g.setColor(Color.white);
			g.setFont(titleFont);
			for(int i = 0; i < options.length; i++) {
				if(i == currentChoice) {
					g.setColor(Color.green);
				} else {
					g.setColor(Color.white);
				}
			g.drawString(options[i], 120, 100 + i * 40);
			}
					
		}
		
			
			
	} else {
		f = 0;
		col = 0;
		fIn = true;
		cIn = true;
		if(gsm.getCurrentState() != 0) {
			rainbows.removeAll(rainbows);
			awesome.setPosition(-80, 100);
			awesome.setVector(3, 0);
		}
	}

	// Fading in
	if(gsm.fadingIn) {
		g.setColor(Color.WHITE);
					
		if (fi <= 1) {
			fi += 0.04;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) fi));
			g.fillRect(0, 0, GamePanel.WIDTH * SCALE, GamePanel.HEIGHT * SCALE);
		} else {
			gsm.unloadState(gsm.getCurrentState());
			gsm.loadState(gsm.getStateToLoad());
			fi = 0;
		} 
					
					
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
					
	}
				
				
	// Fading out
	if(gsm.fadingOut) {
		g.setColor(Color.WHITE);
					
		if (fo - 0.04 >= 0) {
			fo -= 0.04;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) fo));
			g.fillRect(0, 0, GamePanel.WIDTH * SCALE, GamePanel.HEIGHT * SCALE);
		} else {
			gsm.fadingOut = false;
			fo = 1;
		}
				
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
		
		
		
	
	}
	
	
	}
	
	private void drawToScreen() {
		
		BufferedImage manipulatedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		// first, manipulate colors if wished so
		switch (manipulate) {
		case 0:
			manipulatedImage = image;
			break;
		case 1:
			for (int y = 0; y < HEIGHT; y++) {
				for (int x = 0; x < WIDTH; x++) {
					int rgb = image.getRGB(x, y);
					int ir = 255 - ((rgb >> 16) & 0xff);
					int ig = 255 - ((rgb >> 8) & 0xff);
					int ib = 255 - (rgb & 0xff);
					int irgb = (ir << 16) + (ig <<8) + ib;
					manipulatedImage.setRGB(x, y, irgb);
				}
			}
			break;
		
		case 2:
		case 3:
			for (int y = 0; y < HEIGHT; y++) {
				for (int x = 0; x < WIDTH; x++) {
					int rgb = image.getRGB(x, y);
					int value = (((rgb >> 16) & 0xff) + ((rgb >> 8) & 0xff) + (rgb & 0xff)) / 3;
					if (manipulate == 3) value = 255 - value;
					int gsrgb = (value << 16) + (value << 8) + value;
					manipulatedImage.setRGB(x, y, gsrgb);
				}
			}
			break;
		}
		
		// Then pixelate, if wished so
		switch (pixelate) {
		
		case 1:
			int[] argb = new int[4];
			int[] r = new int[4];
			int[] g = new int[4];
			int[] b = new int[4];
			int[] newRGB = new int[3];
			for (int x = 0; x < WIDTH; x += 2) {
				for (int y = 0; y < HEIGHT; y += 2) {
				
					argb[0] = manipulatedImage.getRGB(x, y);
					argb[1] = manipulatedImage.getRGB(x+1, y);
					argb[2] = manipulatedImage.getRGB(x, y+1);
					argb[3] = manipulatedImage.getRGB(x+1, y+1);
					for (int i = 0; i < 4; i++) {
						r[i] = (argb[i] >> 16) & 0xff;
						g[i] = (argb[i] >>  8) & 0xff;
						b[i] = (argb[i]	     ) & 0xff;
					}
					newRGB[0] = (r[0] + r[1] + r[2] + r[3]) / 4; 
					newRGB[1] = (g[0] + g[1] + g[2] + g[3]) / 4;
					newRGB[2] = (b[0] + b[1] + b[2] + b[3]) / 4;
					int rgb = ((newRGB[0] << 16)) + ((newRGB[1] << 8)) + ((newRGB[2]));
					manipulatedImage.setRGB(x, y, rgb);
					manipulatedImage.setRGB(x+1, y, rgb);
					manipulatedImage.setRGB(x, y+1, rgb);
					manipulatedImage.setRGB(x+1, y+1, rgb);
				}
			}
			break;
		
		case 2:
			int[] argb2 = new int[16];
			int[] r2 = new int[16];
			int[] gr2 = new int[16];
			int[] b2 = new int[16];
			int[] newRGB2 = new int[3];
			for (int x = 0; x < WIDTH; x += 4) {
				for (int y = 0; y < HEIGHT; y += 4) {
					int n = 0;
					for (int sx = 0; sx < 4; sx++) {
						for (int sy = 0; sy < 4; sy++) {
							argb2[n] = manipulatedImage.getRGB(x + sx, y + sy);
							n++;
						}
					}
					for (int i = 0; i < 16; i++) {
						r2[i] = (argb2[i] >> 16) & 0xff;
						gr2[i] = (argb2[i] >>  8) & 0xff;
						b2[i] = (argb2[i]	     ) & 0xff;
					}
					int rValue = 0;
					int gValue = 0;
					int bValue = 0;
					for (int i = 0; i < 16; i++) {
						rValue += r2[i];
						gValue += gr2[i];
						bValue += b2[i];
					}
					newRGB2[0] = rValue / 16;
					newRGB2[1] = gValue /16;
					newRGB2[2] = bValue / 16;
					int rgb = ((newRGB2[0] << 16)) + ((newRGB2[1] << 8)) + ((newRGB2[2]));
					for (int sx = 0; sx < 4; sx++) {
						for (int sy = 0; sy < 4; sy++) {
							manipulatedImage.setRGB(x + sx, y + sy, rgb);
						}
					}
				}
			}
			break;
		}		
		
		Graphics g2 = getGraphics();
		g2.drawImage(manipulatedImage, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g2.dispose();
	}
	
	public static void loadWorlds() throws IOException {
		
		// Worlds
		WorldSelectState.unlockedWorlds[0] = Integer.valueOf(properties.getProperty("space"));
		WorldSelectState.unlockedWorlds[1] = Integer.valueOf(properties.getProperty("clouds"));
		WorldSelectState.unlockedWorlds[2] = Integer.valueOf(properties.getProperty("ballon"));
		WorldSelectState.unlockedWorlds[3] = Integer.valueOf(properties.getProperty("overworld"));
		WorldSelectState.unlockedWorlds[4] = Integer.valueOf(properties.getProperty("bunker"));
		WorldSelectState.unlockedWorlds[5] = Integer.valueOf(properties.getProperty("cave"));
		
	}
	
	public static void loadLSCenter() throws IOException {
		for(int i = 0; i < LSCenter.unlockedLevels.length; i++) {
			LSCenter.unlockedLevels[i] = Integer.valueOf(properties.getProperty("c"+String.valueOf(i+1)));
		}
	}
	
	public static String getProperty(String s) { return properties.getProperty(s); }

	@SuppressWarnings("deprecation")
	public static void setSaveProperty(String key, String value) throws IOException {
		properties.setProperty(key, value);
		out = new FileOutputStream("Resources/Saves/Properties.properties");
		properties.save(out, " ");
		out.close();
	}
	
	public void select() {
		
		if(gsm.isGamePaused()) {
		switch(currentChoice) {
			
		case 0:		gsm.resumeCurrentState();
					gsm.setGamePaused(false);
					break;
		case 1:		sfx.get("selected").play();		
					gsm.stopCurrentState();
					if (gsm.getCurrentState() % 10 != 0){
						switch(gsm.getCurrentWorld()) {
						case 1 :	gsm.setState(GameStateManager.LSCENTER);
									break;
						case 2 :	
						case 3 :	
						case 4 :
						case 5 :	
						case 6 :
						case 7 :	break;
						}
					}
					else gsm.setState(GameStateManager.WORLDSELECTSTATE);
					//recovery.play();
					currentChoice = 0;
					break;
		}
		}
		
	}

	public void mute() {
		
	}
	
	public void keyPressed(KeyEvent key) {
		gsm.keyPressed(key.getKeyCode());
		int k = key.getKeyCode();
		
		if(gsm.getCurrentState() > 1) {
		
		if(gsm.isGamePaused()) {
			if(k == KeyEvent.VK_UP) {
				if(currentChoice > 0) {
					sfx.get("select").play();
					currentChoice--;
				}
			}
		
			if(k == KeyEvent.VK_DOWN) {
				if(currentChoice < 1) {
					sfx.get("select").play();
					currentChoice++;
				}
			}
		
		}
		
		}
		
	}
	
	
	
	public void keyReleased(KeyEvent key) {
		gsm.keyReleased(key.getKeyCode());
		int k = key.getKeyCode();
		int s = gsm.getCurrentState();
		
		if(s > 1 && s != 10 && s != 20 && s != 30 && s != 40 && s != 50 && s != 60) {
		
		if(k == KeyEvent.VK_ESCAPE) {
			gsm.setGamePaused(!gsm.isGamePaused());
			if(gsm.isGamePaused()) {
				gsm.stopCurrentState();
			}
			else {
				gsm.resumeCurrentState();
				currentChoice = 0;
			}
		}
		
		if(k == KeyEvent.VK_ENTER) {
			select();
		}
		
		}
		if (k == KeyEvent.VK_P) {
			if (++pixelate == 3) pixelate = 0;
		}
		if (k == KeyEvent.VK_O) {
			if (++manipulate == 4) manipulate = 0;
		}
	}
	public void keyTyped(KeyEvent arg0) { }
	

	
}
