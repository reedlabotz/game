package com.whatever.webviewtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class MainActivity extends Activity {

	private WebView webview;
	private GraphUser userInfo;
	private Session currentSession;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // start Facebook Login
        Session.openActiveSession(this, true, new Session.StatusCallback() {
	        // callback when session changes state
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					currentSession = session;
					// make request to the /me API
					Request.newMeRequest(session, 
						new Request.GraphUserCallback() {
							@Override 
							public void onCompleted(GraphUser user, Response response) {
								if (user != null) {
									Log.d("ASDF", "SUCCESSFULLY LOGGED IN " + response + "    " + user);
									userInfo = user;
									setupWebview();
								} else {
									Toast.makeText(MainActivity.this, "Facebook login failed", Toast.LENGTH_SHORT).show();
								}
							}
						}).executeAsync();
				}
	        }
        });
    }
    
    private void setupWebview() {
        webview = (WebView)findViewById(R.id.main_webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.addJavascriptInterface(this, "hostapp");
        webview.loadUrl(getResources().getString(R.string.app_url));
    }
    
    @JavascriptInterface
    public String getUserId() {
    	if (userInfo == null)
    		return null;
    	return userInfo.getId();
    }
    
    @JavascriptInterface
    public String getAccessToken() {
    	if (currentSession == null)
    		return null;
    	return currentSession.getAccessToken();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
