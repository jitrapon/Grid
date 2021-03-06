package com.code2play.grid;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.code2play.grid.game.GameMode;
import com.code2play.grid.game.GameState;
import com.code2play.grid.game.Grid;

public class GameMain extends Game {

	/** All the screens **/
	GameScreen gameScreen; 

	/** Game grid instance **/
	private Grid grid;

	/** Game modes **/
	private GameMode gameMode;

	/** Game states **/
	private GameState gameState;

	/** DEBUG: whether or not to log FPS **/
	public boolean showFPS;

	/** Game state constants **/
	private static final int DEFAULT_GRID_WIDTH = 4;
	private static final int DEFAULT_GRID_HEIGHT = 4;

	/* Android UI element interface */
	public ActionResolver actionResolver;

	/**
	 * Ctor for Android application
	 * @param actionResolver
	 */
	public GameMain(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}

	/**
	 * Ctor for iOS application
	 */
	public GameMain() {}

	@Override
	public void create () {
		// loading resources, etc.
		Assets.load();
		System.out.println("Launching Grid");

		// DEBUG: set display fps
		showFPS = true;

		// initialize grid
		//TODO done in puzzle choosing screen
		gameMode = GameMode.CHALLENGE;
		gameState = GameState.PLAYING;

		if (gameMode == GameMode.CLASSIC)
			grid = new Grid(this, DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT);			// normal mode
		else if (gameMode == GameMode.CHALLENGE)
			grid = Grid.load(this, Gdx.files.internal("levels/1.lvl"));				// challenge mode

		// set screen TODO mainmenu screen, puzzle choosing screen
		//TODO done in puzzle choosing screen
		gameScreen = new GameScreen(this);	
		
		// this game mode has to be set once main menu
		// screen is chosen, and puzzle file is loaded
		this.setScreen(gameScreen);								// this has to be main menu screen
	}

	/**
	 * Loads a next level by disposing current screen, and create 
	 * a completely new one.
	 * TODO
	 */
	public void loadNextLevel() {
		String nextLevel = "levels/" + (grid.getLevel()+1) + ".lvl";
		if (grid.getLevel()+1 > 7) {
			actionResolver.showShortToast("This level is not available in DEMO version!");
			return;
		}
		
		// load new level file
		grid = null;
		FileHandle fh = Gdx.files.internal(nextLevel);
		grid = Grid.load(this, fh);
		
		// dispose all stuff in this current screen
		gameScreen.dispose();		
		
		// create a new game screen with loaded file
		gameState = GameState.PLAYING;
		gameScreen = new GameScreen(this);
		this.setScreen(gameScreen);
	}

	public GameState getCurrentState() {
		return gameState;
	}

	public void setGameState(GameState state) {
		if (state == GameState.PAUSED) 
			actionResolver.showShortToast("Game Paused");
		else if (state == GameState.PLAYING)
			actionResolver.showShortToast("Game Resumed");
		gameState = state;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameMode mode) {
		gameMode = mode;
	}

	public Grid getGrid() {
		return grid;
	}
	
	public void exit() {
		Gdx.app.exit();
	}

	@Override
	public void render () {
		super.render();
		// check if completed = true
		// if so call loadNextLevel();
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		Assets.dispose();
		gameScreen.dispose();
		Gdx.app.log("DISPOSING", "Released all assets resources");
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
