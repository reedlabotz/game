package com.scribblevine;

import java.util.List;

import org.json.JSONArray;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.facebook.model.GraphUser;

public class GameInterface extends Fragment {
	public static final String FRAGMENT_TAG = "GameInterface";
	private final static String TAG = "GameInterface";
	public static interface GameCallback {
		public void requestedFriendPicker();
	}
	private SessionManager sessionManager;
	private WebView webview;
	private GameCallback callback;
	
	GameInterface(SessionManager sessionManager, GameCallback callback) {
		this.sessionManager = sessionManager;
		this.callback = callback;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	public void setWebview(WebView webview) {
		this.webview = webview;
        webview.addJavascriptInterface(this, "hostapp");
	}
	
	public WebView getRetainedWebview() {
		return webview;
	}
	
	public void unsetWebview() {
		this.webview = null;
	}
	
    @JavascriptInterface
    public String getUserId() {
    	Log.d(TAG, "js: getUserId()");
    	return sessionManager.getUserId();
    }
    
    @JavascriptInterface
    public String getAccessToken() {
    	Log.d(TAG, "js: getAccessToken()");
    	return sessionManager.getAccessToken();
    }
    
    @JavascriptInterface
    public void showFriendPicker() {
    	Log.d(TAG, "js: showFriendPicker()");
    	callback.requestedFriendPicker();
    }
    
    public void finishFriendPicker(List<GraphUser> friends) {
    	if (webview == null) 
    		return;
    	JSONArray friendIds = new JSONArray();
    	for (GraphUser user : friends) {
    		friendIds.put(user.getId());
    	}
    	String js = "javascript:(function(){\n console.log('current friendPicker callback:'+window.finishedFriendPicker);\n window.finishedFriendPicker("+ friendIds.toString() +");\n})()";
    	Log.d(TAG, "JS for finishFriendPicker: "+js);
    	webview.loadUrl(js);
    }
    
    
}
