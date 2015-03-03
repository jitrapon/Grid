package com.code2play.grid.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.code2play.grid.game.Grid;

public class MoveLabel extends Label {

	private Grid grid;
	private float singleDigitPos;
	private float doubleDigitPos;
	
	public MoveLabel(CharSequence text, LabelStyle style, Grid grid, 
			float singlePos, float doublePos) {
		super(text, style);
		this.grid = grid;
		this.singleDigitPos = singlePos;
		this.doubleDigitPos = doublePos;
	}

	@Override
	public void act(final float delta) {
		int movesLeft = grid.getMovesLeft();
		
		// correct digit positions
		setText(movesLeft + "");
		if (movesLeft < 10) 
			setX(singleDigitPos);
		else 
			setX(doubleDigitPos);
			
		super.act(delta);
	}
}
