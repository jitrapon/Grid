package com.code2play.grid.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.ObjectMap;

public class GameDialog extends Dialog {
	
	private Skin skin;
	private BitmapFont titleFont;
	private BitmapFont contentFont;
	private BitmapFont btnFont;
	private ObjectMap<Object, TextButton> value;

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
		
		value = new ObjectMap<Object, TextButton>();
	}
	
	@Override
	public Dialog text(String text) {
		if (skin == null)
			throw new IllegalStateException("This method may only be used if the "
					+ "dialog was constructed with a Skin.");
		LabelStyle style = skin.get(LabelStyle.class);
		style.font = contentFont;
//		style.fontColor.a = 2f;
		return text(text, style);
	}
	
	@Override
	public Dialog button(String text, Object object) {
		if (skin == null)
			throw new IllegalStateException("This method may only be used if the "
					+ "dialog was constructed with a Skin.");
		TextButtonStyle buttonStyle = skin.get(TextButtonStyle.class);
		buttonStyle.up = null;
		buttonStyle.font = btnFont;
		buttonStyle.fontColor.a = 2f;
		
		// add button to key value pair to reference later
		TextButton textButton = new TextButton(text, buttonStyle);
		value.put(object, textButton);
		return button(textButton, object);
	}
	
}
