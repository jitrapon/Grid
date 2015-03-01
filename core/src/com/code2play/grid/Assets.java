package com.code2play.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.code2play.grid.GridBox.Color;

public class Assets {
	
	// main game texture atlas
	private static TextureAtlas atlas;
	
	private static Texture background;
	private static TextureRegion blank;
	private static TextureRegion red;
	private static TextureRegion blue;
	private static TextureRegion green;
	private static TextureRegion yellow;
	
	// change button reference names here
	private static Texture resetBtn;
	private static Texture undoBtn;
	private static Texture swapBtn;
	private static Texture goldMoveBtn;
	private static Texture silverMoveBtn;
	private static Texture bronzeMoveBtn;
	
	// change file names of the images here
	private static final String BLANK = "blank";
	private static final String RED = "red";
	private static final String BLUE = "blue";
	private static final String GREEN = "green";
	private static final String YELLOW = "yellow";
	
	/**
	 * Call this method to load all necessary texture files and etc.
	 */
	public static void load() {
		atlas = new TextureAtlas(Gdx.files.internal("textures/grid.pack"));
		loadTextures();
		loadUI();
	}
	
	/**
	 * Load all textures here
	 */
	private static void loadTextures() {
		// load background
		background = new Texture(Gdx.files.internal("textures/background_temp.png"));
		
		// load grid stuff
		blank = atlas.findRegion(BLANK);
		red = atlas.findRegion(RED);
		blue = atlas.findRegion(BLUE);
		green = atlas.findRegion(GREEN);
		yellow = atlas.findRegion(YELLOW);
	}
	
	/**
	 * Load all UI elements
	 */
	private static void loadUI() {
		resetBtn = new Texture(Gdx.files.internal("ui/reset.png"));
		goldMoveBtn = new Texture(Gdx.files.internal("ui/move_gold.png"));
		silverMoveBtn = new Texture(Gdx.files.internal("ui/move_silver.png"));
		bronzeMoveBtn = new Texture(Gdx.files.internal("ui/move_bronze.png"));
		undoBtn = new Texture(Gdx.files.internal("ui/undo.png"));
		swapBtn = new Texture(Gdx.files.internal("ui/swap.png"));
	}
	
	public static Texture getUndoBtn() {
		return undoBtn;
	}
	
	public static Texture getSwapBtn() {
		return swapBtn;
	}
	
	public static Texture getResetBtn() {
		return resetBtn;
	}
	
	public static Texture getGoldMoveBtn() {
		return goldMoveBtn;
	}
	
	public static Texture getSilverMoveBtn() {
		return silverMoveBtn;
	}
	
	public static Texture getBronzeMoveBtn() {
		return bronzeMoveBtn;
	}
	
	public static TextureRegion getColoredBox(Color color) {
		switch (color) {
		case BLUE:
			return blue;
		case GREEN:
			return green;
		case NONE:
			return blank;
		case RED:
			return red;
		case YELLOW:
			return yellow;
		default:
			return blank;
		}
	}
	
	public static TextureRegion getBlankBox() { 
		return blank;
	}
	
	public static Texture getBackground() {
		return background;
	}
	
	public static void dispose() {
		background.dispose();
		atlas.dispose();
		resetBtn.dispose();
		undoBtn.dispose();
		swapBtn.dispose();
		goldMoveBtn.dispose();
		silverMoveBtn.dispose();
		bronzeMoveBtn.dispose();
	}
}
