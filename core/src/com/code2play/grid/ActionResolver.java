package com.code2play.grid;
	
public interface ActionResolver {
    public static final int VIEW_TEST = 1;
 
    public void showShortToast(CharSequence toastMessage);
    public void showLongToast(CharSequence toastMessage);
    public void showAlertBox(String alertBoxTitle, String alertBoxMessage, String alertBoxButtonText);
    public void openUri(String uri);
    public void showView(final int view);
    public void hideView(final int view);
}