package com.code2play.grid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MoveImage extends Image {
	
	private Grid grid;
	private Drawable gold;
	private Drawable silver;
	private Drawable bronze;
	private CoinType coinType;

	public MoveImage(Grid g, Texture goldTxt, Texture silverTxt, Texture bronzeTxt) {
		super(goldTxt);
		grid = g;
		gold = new TextureRegionDrawable(new TextureRegion(goldTxt));
		silver = new TextureRegionDrawable(new TextureRegion(silverTxt));
		bronze = new TextureRegionDrawable(new TextureRegion(bronzeTxt));
		coinType = CoinType.GOLD;
	}
	
	@Override
	public void act(final float delta) {
		int movesLeft = grid.getMovesLeft();
		if (movesLeft >= grid.getMinGoldMoves() && coinType != CoinType.GOLD) {
			coinType = CoinType.GOLD;
			setDrawable(gold);
		}
		else if (movesLeft < grid.getMinGoldMoves() && movesLeft >= grid.getMinSilverMoves()
				&& coinType != CoinType.SILVER) {
			coinType = CoinType.SILVER;
			setDrawable(silver);
		}
		else if (coinType != CoinType.BRONZE) {
			coinType = CoinType.BRONZE;
			setDrawable(bronze);
		}
		
		super.act(delta);
	}
}
