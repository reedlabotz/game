package com.scribblevine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class SessionManager extends Fragment {
	public static final String FRAGMENT_TAG = "SessionManager";
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
	boolean quiet;
	
	SessionManager(FragmentActivity context, SessionCallback callback) {
		this.context = context;
		uiHelper = new UiLifecycleHelper(context, fbcallback);
		this.callback = callback;
		quiet = false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	    uiHelper.onCreate(savedInstanceState);
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
		if (quiet) {
			return; //Don't send state changes after activity has died.
		}
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        processProfileInfo(session);
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	        callback.loggedOut(this);
	    } else if (!state.isClosed() && state.isOpened()) {
	        Log.i(TAG, "No state at all...");
	        callback.loggedOut(this);
	    }
	}

	
//	private void getAppKey() {
//	    try {
//	        PackageInfo info = context.getPackageManager().getPackageInfo(
//	                "com.scribblevine", 
//	                PackageManager.GET_SIGNATURES);
//	        for (Signature signature : info.signatures) {
//	            MessageDigest md = MessageDigest.getInstance("SHA");
//	            md.update(signature.toByteArray());
//	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//	            }
//	    } catch (NameNotFoundException e) {
//
//	    } catch (NoSuchAlgorithmException e) {
//
//	    }
//	}
	
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
		if (user != null) {
			return; //our work is done.
		}
		Log.d(TAG, this+" Requesting user info: "+user);
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
	
	
	
//	/** Pass-through functions that the parent Activity must call **/
//	protected void onCreateActivity(Bundle savedInstanceState) {
//		uiHelper.onCreate(savedInstanceState);
//	}
//	protected void onResumeActivity() {
//		quiet = false;
//	    // For scenarios where the main activity is launched and user
//	    // session is not null, the session state change notification
//	    // may not be triggered. Trigger it if it's open/closed.
//	    Session session = Session.getActiveSession();
//	    if (session != null &&
//	           (session.isOpened() || session.isClosed()) ) {
//	        onSessionStateChange(session, session.getState(), null);
//	    }
//		uiHelper.onResume();
//	}
//	protected void onPauseActivity() {
//		quiet = true;
//		uiHelper.onResume();
//	}
//	protected void onDestroyActivity() {
//		uiHelper.onDestroy();
//	}
	protected void onActivityResultActivity(int arg0, int arg1, android.content.Intent arg2) {
		uiHelper.onActivityResult(arg0, arg1, arg2);
	}
//	protected void onSaveInstanceStateActivity(Bundle outState) {
//		quiet = true;
//		uiHelper.onSaveInstanceState(outState);
//	}
	protected void onRestoreInstanceStateActivity(Bundle savedInstanceState) {
		quiet = false;
	}

	
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    quiet = false;
	    uiHelper.onResume();
	}

	@Override
	public void onPause() {
	    super.onPause();
	    quiet = true;
	    uiHelper.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
		quiet = true;
	    uiHelper.onSaveInstanceState(outState);
	}
	
	
	
	
	private Session.StatusCallback fbcallback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
}
