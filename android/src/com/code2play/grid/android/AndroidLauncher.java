package com.code2play.grid.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.code2play.grid.GameMain;

public class AndroidLauncher extends AndroidApplication {
	
	ActionResolverAndroid actionResolver;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		actionResolver = new ActionResolverAndroid(this);
		initialize(new GameMain(actionResolver), config);
	}
}
