package com.code2play.grid;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameMain extends Game {
	
	GameScreen gameScreen; 
	private Grid grid;
	private static final int DEFAULT_GRID_WIDTH = 3;
	private static final int DEFAULT_GRID_HEIGHT = 3;
	
	@Override
	public void create () {
		// loading resources, etc.
		Assets.load();
		System.out.println("Launching Grid");
		
		// initialize grid
		grid = new Grid(DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT);
		
		// set screen
		gameScreen = new GameScreen(this);
		this.setScreen(gameScreen);
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
