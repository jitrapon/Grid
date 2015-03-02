package com.code2play.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.code2play.grid.GridBox.Color;

public class Assets {
	
	/** Texture atlas for the game elements (grid) **/
	private static TextureAtlas gridAtlas;
	
	/** Texture atlas for the game ui elements **/
	private static TextureAtlas uiAtlas;
	
	/** Texture for the game background **/
	private static Texture background;
	
	/** Texture for the blank grid **/
	private static TextureRegion blank;
	
	/** Texture for the red grid **/
	private static TextureRegion red;
	
	/** Texture for the blue grid **/
	private static TextureRegion blue;
	
	/** Texture for the green grid **/
	private static TextureRegion green;
	
	/** Texture for the yellow grid **/
	private static TextureRegion yellow;
	
	/** Texture for the reset button in the game screen **/
	private static TextureRegion resetBtn;
	
	/** Texture for the undo button in the game screen **/
	private static TextureRegion undoBtn;
	
	/** Texture for the swap button in the game screen **/
	private static TextureRegion swapBtn;
	
	/** Texture for the gold move label in the game screen **/
	private static TextureRegion goldMoveBtn;
	
	/** Texture for the silver move label in the game screen **/
	private static TextureRegion silverMoveBtn;
	
	/** Texture for the bronze move label in the game screen **/
	private static TextureRegion bronzeMoveBtn;
	
	/** Texture for the game settings **/
	private static TextureRegion gameSettings;
	
	/** File name of the blank image in the grid atlas **/
	private static final String BLANK = "blank";
	
	/** File name of the red image in the grid atlas **/
	private static final String RED = "red";
	
	/** File name of the blue image in the grid atlas **/
	private static final String BLUE = "blue";
	
	/** File name of the green image in the grid atlas **/
	private static final String GREEN = "green";
	
	/** File name of the yellow image in the grid atlas **/
	private static final String YELLOW = "yellow";
	
	/** File name of the bronze move label in the ui atlas **/
	private static final String MOVE_BRONZE = "move_bronze";
	
	/** File name of the gold move label in the ui atlas **/
	private static final String MOVE_GOLD = "move_gold";
	
	/** File name of the silver move label in the ui atlas **/
	private static final String MOVE_SILVER = "move_silver";
	
	/** File name of the game settings in the ui atlas **/
	private static final String GAME_SETTINGS = "game_settings";
	
	/** File name of the reset in the ui atlas **/
	private static final String RESET = "reset";
	
	/** File name of the swap in the ui atlas **/
	private static final String SWAP = "swap";
	
	/** File name of the undo in the ui atlas **/
	private static final String UNDO = "undo";
	
	/**
	 * Call this method to load all necessary texture files and etc.
	 */
	public static void load() {
		gridAtlas = new TextureAtlas(Gdx.files.internal("textures/grid.pack"));
		uiAtlas = new TextureAtlas(Gdx.files.internal("ui/ui.pack"));
		loadTextures();
		loadUI();
	}
	
	/**
	 * Load all textures here
	 */
	private static void loadTextures() {
		// load background
		background = new Texture(Gdx.files.internal("textures/background.png"));
		
		// load grid stuff
		blank = gridAtlas.findRegion(BLANK);
		red = gridAtlas.findRegion(RED);
		blue = gridAtlas.findRegion(BLUE);
		green = gridAtlas.findRegion(GREEN);
		yellow = gridAtlas.findRegion(YELLOW);
	}
	
	/**
	 * Load all UI elements
	 */
	private static void loadUI() {
		resetBtn = uiAtlas.findRegion(RESET);
		goldMoveBtn = uiAtlas.findRegion(MOVE_GOLD);
		silverMoveBtn = uiAtlas.findRegion(MOVE_SILVER);
		bronzeMoveBtn = uiAtlas.findRegion(MOVE_BRONZE);
		undoBtn = uiAtlas.findRegion(UNDO);
		swapBtn = uiAtlas.findRegion(SWAP);
		gameSettings = uiAtlas.findRegion(GAME_SETTINGS);
	}
	
	public static TextureRegion getUndoBtn() {
		return undoBtn;
	}
	
	public static TextureRegion getSwapBtn() {
		return swapBtn;
	}
	
	public static TextureRegion getResetBtn() {
		return resetBtn;
	}
	
	public static TextureRegion getGoldMoveBtn() {
		return goldMoveBtn;
	}
	
	public static TextureRegion getSilverMoveBtn() {
		return silverMoveBtn;
	}
	
	public static TextureRegion getBronzeMoveBtn() {
		return bronzeMoveBtn;
	}
	
	public static TextureRegion getGameSettingsBtn() {
		return gameSettings;
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
		gridAtlas.dispose();
		uiAtlas.dispose();
	}
}
