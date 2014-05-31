package GameState;

import java.awt.Graphics2D;

import Audio.AudioPlayer;

public class GameStateManager {
	
	private GameState[] gameStates;
	private int currentState;
	private int stateToLoad;
	
	public static final int NUMGAMESTATES = 16;
	public static final int MENUSTATE = 0;
	public static final int WORLDSELECTSTATE = 1;
	public static final int LEVELSELECTSTATE = 2;
	public static final int CENTERSTATE = 10;
	public static final int CAVESTATE = 11;
	public static final int BUNKERSTATE = 12;
	public static final int OVERWORLDSTATE = 13;
	public static final int BALLONSTATE = 14;
	public static final int CLOUDSSTATE = 15;
	public static final int SPACESTATE = 16;
	
	private boolean GAMEPAUSED = false;
	public boolean MUTE = false;
	public static boolean loadingScreen;
	public boolean fadingIn;
	public boolean fadingOut;
	
	public AudioPlayer menuTheme;
	
	
	
	public GameStateManager() {
		
		
		gameStates = new GameState[NUMGAMESTATES];
		
		currentState = CENTERSTATE;
		loadState(currentState);
		
	}
	
	public void  loadState(int state) {
		
		if(state == MENUSTATE)			gameStates[state] = new MenuState(this);
		if(state == WORLDSELECTSTATE)	gameStates[state] = new WorldSelectState(this);
		if(state == LEVELSELECTSTATE)	gameStates[state] = new LevelSlctState(this);
		if(state == CENTERSTATE)		gameStates[state] = new CenterState(this);
		if(state == CAVESTATE)			gameStates[state] = new CaveState(this);
		if(state == BUNKERSTATE)		gameStates[state] = new BunkerState(this);
		if(state == OVERWORLDSTATE)		gameStates[state] = new OverworldState(this);
		if(state == BALLONSTATE)		gameStates[state] = new BallonState(this);
		if(state == CLOUDSSTATE)		gameStates[state] = new CloudsState(this);
		if(state == SPACESTATE)			gameStates[state] = new SpaceState(this);
		currentState = state;
		
		fadingIn = false;
		fadingOut = true;
		loadingScreen = false;
		
	}
	
	public void unloadState(int state) {
		
		gameStates[state] = null;
	
	}
	
	public void setState(int state) {
		loadingScreen = true;
		GAMEPAUSED = false;
		fadingIn = true;
		stateToLoad = state;
		
	}
	
	public void stopCurrentState() {
		gameStates[currentState].stop();
	}
	public void resumeCurrentState() {
		gameStates[currentState].resume();
	}
	
	public void update() {
		try {
			gameStates[currentState].update();
		} catch(Exception e) {}
	}
	
	
	public void draw(Graphics2D g) {
		
		try {
			gameStates[currentState].draw(g);
		} catch(Exception e) {}
		
	}
	
	

	
	public int getCurrentState() { return currentState; }
	public int getStateToLoad() { return stateToLoad; }
	public boolean isGamePaused() { return GAMEPAUSED; }
	public boolean isMute() { return MUTE; }
	
	public void setGamePaused(boolean p) {
		GAMEPAUSED = p;
	}
	public void setMute(boolean m) {
		MUTE = m;
	}
	
	
	
	public void keyPressed(int k) {
		gameStates[currentState].keyPressed(k);
	}
	
	public void keyReleased(int k) {
		gameStates[currentState].keyReleased(k);
	}
	

}
