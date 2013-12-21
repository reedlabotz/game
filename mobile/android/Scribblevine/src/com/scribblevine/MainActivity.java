package com.scribblevine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;

import com.scribblevine.SessionManager.SessionCallback;

public class MainActivity extends FragmentActivity implements SessionCallback {
	private static final String TAG = "MainActivity";
	SessionManager sessionManager;
	GameInterface gameInterface;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initManagers(savedInstanceState);
	}
	
	private void initManagers(Bundle savedInstanceState) {
		sessionManager = new SessionManager(this, this);
		sessionManager.onCreate(savedInstanceState);
		sessionManager.init();
		gameInterface = new GameInterface(sessionManager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	public void onResume() {
	    super.onResume();
	    sessionManager.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    sessionManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    sessionManager.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    sessionManager.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    sessionManager.onSaveInstanceState(outState);
	}
	
	
	
	public void loggedOut(SessionManager manager) {
		Log.d(TAG, "Showing facebook login fragment");
		FacebookLoginFragment fragment = new FacebookLoginFragment();
		fragment.setSessionManager(manager);
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(null)
                .commit();
        // We want the fragment fully created so we can use it immediately.
        fm.executePendingTransactions();
	}
	
	public void loggedIn(SessionManager manager) {
		Log.d(TAG, "Hiding facebook login fragment");
		WebviewGameFragment fragment = new WebviewGameFragment();
		fragment.setGameInterface(gameInterface);
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(null)
                .commit();
        // We want the fragment fully created so we can use it immediately.
        fm.executePendingTransactions();

	}

}
