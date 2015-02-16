package com.code2play.grid;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
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
	}
}
