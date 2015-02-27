package com.code2play.grid;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class SwapLabel extends Label {

	private Grid grid;
	private float singleDigitPos;
	private float doubleDigitPos;

	public SwapLabel(CharSequence text, LabelStyle style, Grid grid, 
			float singleDigitPos, float doubleDigitPos) {
		super(text, style);
		this.grid = grid;
		this.singleDigitPos = singleDigitPos;
		this.doubleDigitPos = doubleDigitPos;
	}

	@Override
	public void act(final float delta) {
		int swap = grid.getNumSwapsLeft();
		setText(swap + "");
		if (swap < 10) 
			setX(singleDigitPos);
		else 
			setX(doubleDigitPos);
		
		super.act(delta);
	}
}
