package com.scribblevine;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;

import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PickerFragment.OnDoneButtonClickedListener;
import com.scribblevine.GameInterface.GameCallback;
import com.scribblevine.SessionManager.SessionCallback;

public class MainActivity extends FragmentActivity implements SessionCallback, GameCallback {
	private static final String TAG = "MainActivity";
	SessionManager sessionManager;
	GameInterface gameInterface;
	WebviewGameFragment gameFragment;
	
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
		gameInterface = new GameInterface(sessionManager, this);
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
	
	
	
	public void loggedOut(final SessionManager manager) {
		runOnUiThread(new Runnable() {
			public void run() {
				loggedOutUiThread(manager);
			}
		});
	}
	
	private void loggedOutUiThread(SessionManager manager) {
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
	
	public void loggedIn(final SessionManager manager) {
		runOnUiThread(new Runnable() {
			public void run() {
				loggedInUiThread(manager);
			}
		});
	}
	
	private void loggedInUiThread(SessionManager manager) {
		Log.d(TAG, "Hiding facebook login fragment - showing webview");
		if (gameFragment == null) {
			gameFragment = new WebviewGameFragment();
		}
		gameFragment.setGameInterface(gameInterface);
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_holder, gameFragment)
                .addToBackStack(null)
                .commit();
        // We want the fragment fully created so we can use it immediately.
        fm.executePendingTransactions();
	}

	@Override
	public void requestedFriendPicker() {
		runOnUiThread(new Runnable() {
			public void run() {
				requestedFriendPickerUiThread();
			}
		});
	}
	
	private void requestedFriendPickerUiThread() {
		Log.d(TAG, "Showing friend picker fragment");
		FriendPickerFragment fragment = new FriendPickerFragment();
		fragment.setMultiSelect(true);
        FragmentManager fm = this.getSupportFragmentManager();
        fragment.setOnDoneButtonClickedListener(new OnDoneButtonClickedListener() {
			@Override
			public void onDoneButtonClicked(PickerFragment<?> fragment) {
				FriendPickerFragment friendFragment = (FriendPickerFragment) fragment;
				List<GraphUser> friends = friendFragment.getSelection();
				friendListPicked(friends);
			}
		});
        fm.beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack("")
                .commit();
        // We want the fragment fully created so we can use it immediately.
        fm.executePendingTransactions();
		fragment.loadData(false);
	}
	
	
	private void friendListPicked(final List<GraphUser> friends) {
		runOnUiThread(new Runnable() {
			public void run() {
				friendListPickedUiThread(friends);
			}
		});
	}
	
	private void friendListPickedUiThread(List<GraphUser> friends) {
		Log.d(TAG, "Friend picker finished: "+friends);
		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
		fm.popBackStack();
		fm.executePendingTransactions();
		gameInterface.finishFriendPicker(friends);
	}

}
