package com.code2play.grid.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class GameDialog extends Dialog {
	
	private Skin skin;
	private BitmapFont titleFont;
	private BitmapFont contentFont;
	private BitmapFont btnFont;
	private Label content;

	public GameDialog(String title, Skin skin, BitmapFont titleFont, BitmapFont contentFont, BitmapFont btnFont,
			String windowStyleName) {
		super(title, skin, windowStyleName);
		this.titleFont = titleFont;
		this.contentFont = contentFont;
		this.btnFont = btnFont;
		this.skin = skin;
		
		WindowStyle style = skin.get(windowStyleName, WindowStyle.class);
		style.titleFont = this.titleFont;	
		
		setStyle(style);
	}
	
	public GameDialog text(String text, float wordWrapWidth) {
		if (skin == null)
			throw new IllegalStateException("This method may only be used if the "
					+ "dialog was constructed with a Skin.");
		LabelStyle style = skin.get(LabelStyle.class);
		style.font = contentFont;
//		style.fontColor.a = 2f;
		
		return text(text, style, wordWrapWidth);
	}
	
	public GameDialog text(String text, LabelStyle style, float width) {
		if (content != null) {
			this.getContentTable().removeActor(content);
			content.setText(text);
		}
		else {
			content = new Label(text, style);
			content.setWrap(true);
			content.setAlignment(Align.center);
		}
		this.getContentTable().padTop(100).padBottom(100).add(content).width(width).row();
		return this;
	}
	
	public Dialog button(String text, Object object, float width) {
		if (skin == null)
			throw new IllegalStateException("This method may only be used if the "
					+ "dialog was constructed with a Skin.");
		TextButtonStyle buttonStyle = skin.get(TextButtonStyle.class);
		buttonStyle.up = null;
		buttonStyle.font = btnFont;
		buttonStyle.fontColor.a = 2f;
		
		// add button to key value pair to reference later
		TextButton textButton = new TextButton(text, buttonStyle);
		
		if (width <= 0)
			return button(textButton, object);
		else 
			return button(textButton, object, width);
	}
	
	public GameDialog button(Button button, Object object, float width) {
		this.getButtonTable().add(button).width(width);
		setObject(button, object);
		return this;
	}
	
}
