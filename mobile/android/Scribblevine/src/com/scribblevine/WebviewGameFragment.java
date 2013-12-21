package com.scribblevine;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebviewGameFragment extends Fragment {
	WebView webview;
	GameInterface gameInterface;
	
	public void setGameInterface(GameInterface gameInterface) {
		this.gameInterface = gameInterface;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.webview_fragment, null);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onStart() {
		super.onStart();
        webview = (WebView)getView().findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.addJavascriptInterface(gameInterface, "hostapp");
        webview.loadUrl(getResources().getString(R.string.app_url));
	}	
}
