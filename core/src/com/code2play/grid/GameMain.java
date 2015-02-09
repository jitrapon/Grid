package com.code2play.grid;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.code2play.grid.GridBox.Color;

public class GameMain extends Game {
	
	/** All the screens **/
	GameScreen gameScreen; 
	
	/** Game states **/
	private Grid grid;
	private GameMode gameMode;
	private GameState gameState;
	
	/** Game state constants **/
	private static final int DEFAULT_GRID_WIDTH = 4;
	private static final int DEFAULT_GRID_HEIGHT = 4;
	
	@Override
	public void create () {
		// loading resources, etc.
		Assets.load();
		System.out.println("Launching Grid");
		
		// initialize grid
		//TODO done in puzzle choosing screen
		gameMode = GameMode.CHALLENGE;
		gameState = GameState.PLAYING;
		
//		grid = new Grid(this, DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT);		// normal mode
		grid = Grid.load(this, Gdx.files.internal("1.lvl"));				// challenge mode
		
		// set screen TODO mainmenu screen, puzzle choosing screen
		//TODO done in puzzle choosing screen
		gameScreen = new GameScreen(this);	
																// this game mode has to be set once main menu
																// screen is chosen, and puzzle file is loaded
		this.setScreen(gameScreen);								// this has to be main menu screen
	}
	
	public GameState getCurrentState() {
		return gameState;
	}
	
	public void setGameState(GameState state) {
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

	@Override
	public void render () {
		super.render();
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
