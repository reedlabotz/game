package com.scribblevine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.widget.LoginButton;

public class FacebookLoginFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.facebook_login_fragment, null);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		LoginButton loginButton = (LoginButton) getView().findViewById(R.id.facebook_login_button);
	}
}
