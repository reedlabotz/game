package com.scribblevine;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
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
		getAppKey();
        // Try to log in to facebook - if there's no auth token, bump them to the login fragment.
		this.session = Session.openActiveSessionFromCache(context);
		if (session == null) {
			showFacebookLogin();
		}
//        Session.openActiveSession(context, true, new Session.StatusCallback() {
//	        // callback when session changes state
//	        @Override
//	        public void call(Session session, SessionState state, Exception exception) {
//	        	Log.d(TAG, "FB Session change: "+state);
//				if (session.isOpened()) {
//					processProfileInfo(session);
//				} else {
//					showFacebookLogin();
//				}
//	        }
//        });

	}
	
	public void checkLoginState() {
		Log.d(TAG, "CHECK LOGIN STATE");
		processProfileInfo(session);
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
		FacebookLoginFragment fragment = new FacebookLoginFragment();
		fragment.setSessionManager(this);
        FragmentManager fm = context.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(null)
                .commit();
        // We want the fragment fully created so we can use it immediately.
        fm.executePendingTransactions();
	}
	
}
