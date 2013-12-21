package com.scribblevine;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class SessionManager {
	private static final String TAG = "SessionManager";
	public static interface SessionCallback {
		public void loggedIn(SessionManager manager);
		public void loggedOut(SessionManager manager);
	}
	FragmentActivity context;
	SessionCallback callback;
	UiLifecycleHelper uiHelper;
	Session session;
	GraphUser user;
	
	SessionManager(FragmentActivity context, SessionCallback callback) {
		this.context = context;
		uiHelper = new UiLifecycleHelper(context, fbcallback);
		this.callback = callback;
	}
	
	public void init() {
        // Try to log in to facebook - if there's no auth token, bump them to the login fragment.
		this.session = Session.openActiveSessionFromCache(context);
		if (session == null) {
			Log.d(TAG, "Session is null - showing login screen");
	        Session.openActiveSession(context, false, fbcallback);
			callback.loggedOut(this);
		}
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        processProfileInfo(session);
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	        callback.loggedOut(this);
	    }
	}

	
	private void getAppKey() {
	    try {
	        PackageInfo info = context.getPackageManager().getPackageInfo(
	                "com.scribblevine", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }
	}
	
    public String getUserId() {
    	if (user == null || !isLoggedIn())
    		return null;
    	return user.getId();
    }
    
    public String getAccessToken() {
    	if (session == null || !isLoggedIn())
    		return null;
    	return session.getAccessToken();
    }
    
    public boolean isLoggedIn() {
    	return session.isOpened();
    }
	
	private void processProfileInfo(Session session) {
		Request.newMeRequest(session, 
			new Request.GraphUserCallback() {
				@Override 
				public void onCompleted(GraphUser user, Response response) {
					Log.d(TAG, "On completed: "+user);
					if (user != null) {
						Log.d(TAG, "SUCCESSFULLY LOGGED IN " + response + "    " + user);
						SessionManager.this.user = user;
						callback.loggedIn(SessionManager.this);
					} else {
						Toast.makeText(context, "Facebook login failed", Toast.LENGTH_SHORT).show();
					}
				}
			}).executeAsync();
	}
	
	
	
	/** Pass-through functions that the parent Activity must call **/
	protected void onCreate(Bundle savedInstanceState) {
		uiHelper.onCreate(savedInstanceState);
	}
	protected void onResume() {
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
		uiHelper.onResume();
	}
	protected void onPause() {
		uiHelper.onResume();
	}
	protected void onDestroy() {
		uiHelper.onDestroy();
	}
	protected void onActivityResult(int arg0, int arg1, android.content.Intent arg2) {
		uiHelper.onActivityResult(arg0, arg1, arg2);
	}
	protected void onSaveInstanceState(Bundle outState) {
		uiHelper.onSaveInstanceState(outState);
	}
	
	private Session.StatusCallback fbcallback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
}
