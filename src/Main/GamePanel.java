package Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;

import GameState.GameStateManager;
import GameState.WorldSelectState;


@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener{
	
	
	
	// dimensions
	public static final int WIDTH = 400;
	public static final int HEIGHT = 280;
	public static final int SCALE = 2;
	
	// game thread
	private Thread thread;
	private boolean running;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;
	
	// image
	private BufferedImage image;
	public static Graphics2D g;
	
	// fading & pause stuff
	boolean fIn = true;
	boolean cIn = true;
	int col;
	double f = 0;
	double fi = 0;
	double fo = 1;
	
	public JButton[] buttons = new JButton[1];
	
	// game state manager
	private GameStateManager gsm;
	
	// saves
	static FileInputStream in;
	
	
	
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
			
			loadSaves();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		running = true;
		
		gsm = new GameStateManager();
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
				g.fillRect(0, 0, GamePanel.WIDTH * SCALE, GamePanel.HEIGHT * SCALE);
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
				g.fillRect(0, 0, GamePanel.WIDTH * SCALE, GamePanel.HEIGHT * SCALE);
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
		//g.drawString("Press M to change Audio settings", 160, 240);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			
			
			
	} else {
		f = 0;
		col = 0;
		fIn = true;
		cIn = true;
	}
		
		
	// Fading in
	if(gsm.fadingIn) {
		g.setColor(Color.WHITE);
					
		if (fi + 0.04 <= 1) {
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
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);				// scales the image up to fullscreen
		g2.dispose();
	}
	
	public static void loadSaves() throws IOException {
		
		Properties properties = new Properties();
		in = new FileInputStream("Resources/Saves/Properties.properties");
		properties.load(in);
		in.close();
		WorldSelectState.unlockedWorlds[0] = Integer.valueOf(properties.getProperty("space"));
		WorldSelectState.unlockedWorlds[1] = Integer.valueOf(properties.getProperty("clouds"));
		WorldSelectState.unlockedWorlds[2] = Integer.valueOf(properties.getProperty("ballon"));
		WorldSelectState.unlockedWorlds[3] = Integer.valueOf(properties.getProperty("overworld"));
		WorldSelectState.unlockedWorlds[4] = Integer.valueOf(properties.getProperty("bunker"));
		WorldSelectState.unlockedWorlds[5] = Integer.valueOf(properties.getProperty("cave"));
		WorldSelectState.unlockedWorlds[6] = Integer.valueOf(properties.getProperty("center"));
		
	}
	
	public void keyPressed(KeyEvent key) {
		gsm.keyPressed(key.getKeyCode());
	}
	public void keyReleased(KeyEvent key) {
		gsm.keyReleased(key.getKeyCode());
	}
	public void keyTyped(KeyEvent arg0) {}
	

	
}
