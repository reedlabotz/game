package com.scribblevine;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebviewGameFragment extends Fragment {
	private static final String TAG = "WebviewGameFragment";
	WebView webview;
	GameInterface gameInterface;
	Bundle webBundle;
	
	public void setGameInterface(GameInterface gameInterface) {
		this.gameInterface = gameInterface;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "Webview Create: parent:"+container);
		if (webview == null) {
			webview = new WebView(getActivity());
	        WebSettings webSettings = webview.getSettings();
	        webSettings.setJavaScriptEnabled(true);
	        webview.loadUrl(getResources().getString(R.string.app_url));
		} else if (webview.getParent() instanceof ViewGroup){
			((ViewGroup) webview.getParent()).removeView(webview);
		}
        return webview;
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Webview Start");
        gameInterface.setWebview(webview);
	}	
	
	@Override
	public void onPause() {
		super.onPause();
		
		webview.saveState(webBundle);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "Webview Stop");
		gameInterface.unsetWebview();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Webview Destroy");
	}
}
