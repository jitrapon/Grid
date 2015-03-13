package com.code2play.grid.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class BlurImage extends Image {

	private ShaderProgram blurShader;
	private ShaderProgram overlayShader;

	private static final float MAX_BLUR = 14f;
	private static final float GREY_SCALE = 5.0f;

	private FrameBuffer fboA;
	private FrameBuffer fboB;

	boolean run;


	public BlurImage(TextureRegion region, ShaderProgram blurShader, ShaderProgram overlayShader, FrameBuffer fboA,
			FrameBuffer fboB) {
		super(region);
		this.blurShader = blurShader;
		this.overlayShader = overlayShader;
		this.fboA = fboA;
		this.fboB = fboB;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (!run) {
			batch.setShader(blurShader);

			blurShader.setUniformf("resolution", Gdx.graphics.getHeight());

			//apply the blur only along Y-axis
			blurShader.setUniformf("dir", 0f, 1f);

			// set blur radius on x-ais
			blurShader.setUniformf("radius", MAX_BLUR);

			fboA.begin();
			// 'record' the drawing onto this temp fbo
			//		super.draw(batch, parentAlpha);
			batch.end();
			batch.begin();
			this.getDrawable().draw(batch, 0, 0, this.getStage().getWidth(), this.getStage().getHeight());
			batch.end();
			fboA.end();
			
			TextureRegion region = new TextureRegion(fboA.getColorBufferTexture());
			this.setDrawable(new TextureRegionDrawable(region));
			
			//apply the blur only along Y-axis
			blurShader.begin();
			blurShader.setUniformf("dir", 1f, 0f);
			blurShader.end();
			
			fboB.begin();
			// 'record' the drawing onto this temp fbo
			//		super.draw(batch, parentAlpha);
			batch.begin();
			this.getDrawable().draw(batch, 0, 0, this.getStage().getWidth(), this.getStage().getHeight());
			batch.end();
			fboB.end();
			
			region = new TextureRegion(fboB.getColorBufferTexture());
			this.setDrawable(new TextureRegionDrawable(region));
			batch.begin();
			super.draw(batch, parentAlpha);
			run = true;
		}

		else 
			super.draw(batch, parentAlpha);

		batch.setShader(null);
	}
}
