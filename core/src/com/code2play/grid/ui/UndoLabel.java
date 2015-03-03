package com.code2play.grid.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.code2play.grid.game.Grid;

public class UndoLabel extends Label {

	private Grid grid;

	public UndoLabel(CharSequence text, LabelStyle style, Grid grid) {
		super(text, style);
		this.grid = grid;
	}

	@Override
	public void act(final float delta) {
		int undoCount = grid.getUndoCount();
		setText(undoCount + "");
		super.act(delta);
	}
}