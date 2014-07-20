package GameState;

import java.awt.Graphics2D;

import Audio.AudioPlayer;

public class GameStateManager {
	
	private GameState[] gameStates;
	private int currentState;
	private int stateToLoad;
	private int currentWorld;
	
	public static final int NUMGAMESTATES = 91;
	public static final int MENUSTATE = 0;
	public static final int WORLDSELECTSTATE = 1;
	public static final int LSCENTER = 10;
	public static final int CENTERSTATE = 11;
	
	public static final int CAVESTATE = 21;
	public static final int BUNKERSTATE = 31;
	public static final int OVERWORLDSTATE = 41;
	public static final int BALLONSTATE = 51;
	public static final int CLOUDSSTATE = 61;
	public static final int SPACESTATE = 71;
	
	private boolean GAMEPAUSED = false;
	public static boolean loadingScreen;
	public boolean fadingIn;
	public boolean fadingOut;
	
	public AudioPlayer menuTheme;
	
	
	
	public GameStateManager() {
		
		
		gameStates = new GameState[NUMGAMESTATES];
		
		currentState = LSCENTER;;
		setState(currentState);
		
	}
	
	public void  loadState(int state) {
		
		if(state == MENUSTATE)			gameStates[state] = new MenuState(this);
		if(state == WORLDSELECTSTATE)	gameStates[state] = new WorldSelectState(this);
		if(state == LSCENTER)			gameStates[state] = new LevelSelections.LSCenter(this);
		if(state == CENTERSTATE)		{
			gameStates[state] = new CenterState(this);
			currentWorld = 1;
		}
		if(state == CAVESTATE)			{
			gameStates[state] = new CaveState(this);
			currentWorld = 2;
		}
		if(state == BUNKERSTATE)		{
			gameStates[state] = new BunkerState(this);
			currentWorld = 3;
		}
		if(state == OVERWORLDSTATE)		{
			gameStates[state] = new OverworldState(this);
			currentWorld = 4;
		}
		if(state == BALLONSTATE) {
			gameStates[state] = new BallonState(this);
			currentWorld = 5;
		}
		if(state == CLOUDSSTATE) {
			gameStates[state] = new CloudsState(this);
			currentWorld = 6;
		}
		if(state == SPACESTATE) {
			gameStates[state] = new SpaceState(this);
			currentWorld = 7;
		}
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
	public int getCurrentWorld() { return currentWorld; }
	public int getStateToLoad() { return stateToLoad; }
	public boolean isGamePaused() { return GAMEPAUSED; }

	public void setGamePaused(boolean p) {
		GAMEPAUSED = p;
	}
	
	public void keyPressed(int k) {
		gameStates[currentState].keyPressed(k);
	}
	
	public void keyReleased(int k) {
		gameStates[currentState].keyReleased(k);
	}
	

}
