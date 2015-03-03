package com.code2play.grid.game;

import com.badlogic.gdx.utils.Array;
import com.code2play.grid.game.GridBox.Color;

public class Group extends Array<Object> {
	
	private Color color;
	
	public Group(Color color) {
		super();
		this.color = color;
	}
	
	public Group(int capacity, Color color) {
		super(capacity);
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
}
