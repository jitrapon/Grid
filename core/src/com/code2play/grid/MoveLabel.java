package com.code2play.grid;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class MoveLabel extends Label {

	private Grid grid;

	public MoveLabel(CharSequence text, LabelStyle style, Grid grid) {
		super(text, style);
		this.grid = grid;
	}

	@Override
	public void act(final float delta) {
		int movesLeft = grid.getMovesLeft();
		setText(movesLeft + "");
		super.act(delta);
	}
}
