package edu.utexas.colin.chaacapp.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

	private boolean mDestroyed;
	protected View mView;

	protected abstract int getLayoutResID();

	public boolean isDestroyed() {
		return mDestroyed;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(getLayoutResID(), container, false);

		ButterKnife.bind(this, mView);

		return mView;
	}

	@Override
	public void onDestroy() {
		mDestroyed = true;
		super.onDestroy();
	}
}
