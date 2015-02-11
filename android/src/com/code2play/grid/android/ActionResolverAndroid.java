package com.code2play.grid.android;

import com.code2play.grid.ActionResolver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

public class ActionResolverAndroid implements ActionResolver {

	Handler uiThread;
	Context appContext;

	public ActionResolverAndroid(Context appContext) {
		uiThread = new Handler();
		this.appContext = appContext;
	}


	@Override
	public void showShortToast(final CharSequence toastMessage) {

		uiThread.post(new Runnable() {
			public void run() {
				Toast.makeText(appContext, toastMessage, Toast.LENGTH_SHORT)
				.show();
			}
		});
	}

	@Override
	public void showLongToast(final CharSequence toastMessage) {
		uiThread.post(new Runnable() {
			public void run() {
				Toast.makeText(appContext, toastMessage, Toast.LENGTH_LONG)
				.show();
			}
		});
	}

	@Override
	public void showAlertBox(final String alertBoxTitle, final String alertBoxMessage,
			final String alertBoxButtonText) {
		uiThread.post(new Runnable() {
			public void run() {
				new AlertDialog.Builder(appContext)
				.setTitle(alertBoxTitle)
				.setMessage(alertBoxMessage)
				.setNeutralButton(alertBoxButtonText,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
					}
				}).create().show();
			}
		});

	}

	@Override
	public void openUri(String uri) {
		Uri myUri = Uri.parse(uri);
		Intent intent = new Intent(Intent.ACTION_VIEW, myUri);
		appContext.startActivity(intent);
	}

	@Override
	public void showView(int view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideView(int view) {
		// TODO Auto-generated method stub

	}
}
