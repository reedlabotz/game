package com.scribblevine;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class SessionManager {
	private static final String TAG = "SessionManager";
	FragmentActivity context;
	Session session;
	GraphUser user;
	
	SessionManager(FragmentActivity context) {
		this.context = context;
	}
	
	public void init() {
        // Try to log in to facebook - if there's no auth token, bump them to the login fragment.
        Session.openActiveSession(context, new FacebookLoginFragment(), true, new Session.StatusCallback() {
	        // callback when session changes state
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
	        	Log.d(TAG, "FB Session change: "+state);
	        	SessionManager.this.session = session;
				if (session.isOpened()) {
					processProfileInfo(session);
				} else {
					showFacebookLogin();
				}
	        }
        });
	}
	
	private void processProfileInfo(Session session) {
		Request.newMeRequest(session, 
			new Request.GraphUserCallback() {
				@Override 
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						Log.d(TAG, "SUCCESSFULLY LOGGED IN " + response + "    " + user);
						SessionManager.this.user = user;
					} else {
						Toast.makeText(context, "Facebook login failed", Toast.LENGTH_SHORT).show();
					}
				}
			}).executeAsync();
	}
	
	private void showFacebookLogin() {
		Log.d(TAG, "Showing facebook login fragment");
        FragmentManager fm = context.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_holder, new FacebookLoginFragment())
                .addToBackStack(null)
                .commit();
        // We want the fragment fully created so we can use it immediately.
        fm.executePendingTransactions();

	}
	
}
