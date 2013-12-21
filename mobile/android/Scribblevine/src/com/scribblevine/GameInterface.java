package com.scribblevine;

import android.webkit.JavascriptInterface;

public class GameInterface {
	private SessionManager sessionManager;
	
	GameInterface(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
    @JavascriptInterface
    public String getUserId() {
    	return sessionManager.getUserId();
    }
    
    @JavascriptInterface
    public String getAccessToken() {
    	return sessionManager.getAccessToken();
    }
}
