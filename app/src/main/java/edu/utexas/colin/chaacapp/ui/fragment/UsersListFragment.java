package edu.utexas.colin.chaacapp.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.model.ChaacModel;
import edu.utexas.colin.chaacapp.model.UsersListener;
import edu.utexas.colin.chaacapp.model.users.User;
import edu.utexas.colin.chaacapp.ui.activity.UserFormActivity;
import edu.utexas.colin.chaacapp.ui.activity.UserInfoActivity;
import edu.utexas.colin.chaacapp.ui.recycler.OnItemClickListener;
import edu.utexas.colin.chaacapp.ui.recycler.UsersAdapterHolder;

public class UsersListFragment extends BaseFragment implements UsersListener {

	private static final String TAG = "UserListActivity";

	@BindView(R.id.users_recycler_view)
	RecyclerView mRecycler;

	public UsersListFragment() {

	}

	@Override
	protected int getLayoutResID() {
		return R.layout.fragment_users_list;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		UsersAdapterHolder adapter = new UsersAdapterHolder(ChaacModel.model().getUsers(), getActivity());
		adapter.setOnItemClickListener(mItemClickListener);
		mRecycler.setAdapter(adapter);

		ChaacModel.model().addUsersListener(this);

		return view;
	}

	@Override
	public void onDestroyView() {
		ChaacModel.model().removeUsersListener(this);

		super.onDestroyView();
	}

	@Override
	public void notifyUsersChanged() {
		UsersAdapterHolder adapter = (UsersAdapterHolder) mRecycler.getAdapter();
		adapter.update(ChaacModel.model().getUsers());
	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			UsersAdapterHolder adapter = (UsersAdapterHolder) mRecycler.getAdapter();
			User user = adapter.getUser(position);
			Intent intent = new Intent(getActivity(), UserInfoActivity.class);
			intent.putExtra(UserFormActivity.USER_ID_KEY, user.getId());
			startActivity(intent);
		}
	};
}
