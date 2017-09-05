package edu.utexas.colin.chaacapp.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.client.DataletsRestClient;
import edu.utexas.colin.chaacapp.model.ChaacModel;
import edu.utexas.colin.chaacapp.model.datalets.Datalet;
import edu.utexas.colin.chaacapp.model.users.User;
import edu.utexas.colin.chaacapp.ui.BaseActivity;
import edu.utexas.colin.chaacapp.ui.recycler.OnItemClickListener;
import edu.utexas.colin.chaacapp.ui.recycler.UsersAdapterHolder;

public class DataletInfoActivity extends BaseActivity implements View.OnClickListener {

	public static final String DATALET_ID_KEY = "dataletID";
	public static final String TAG = "DataletInfoActivity";

	private Datalet mDatalet;
	private List<User> mUsersWithAccess = new ArrayList<>();

	@BindView(R.id.datalet_info_title)
	TextView mTitle;

	@BindView(R.id.datalet_info_description)
	TextView mDescription;

	@BindView(R.id.datalet_view_on_map)
	Button mViewOnMap;

	@BindView(R.id.datalet_info_users_access)
	RecyclerView mUsersWithAccessRecycler;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_datalet_info;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String dataletID = intent.getStringExtra(DATALET_ID_KEY);
		getSupportActionBar().setElevation(0);
		mDatalet = ChaacModel.model().getDatalet(dataletID);
		updateDataletDisplay(mDatalet);

		mViewOnMap.setOnClickListener(this);

		mUsersWithAccessRecycler.setLayoutManager(new LinearLayoutManager(this));
		UsersAdapterHolder accessAdapter = new UsersAdapterHolder(new ArrayList<>(), this);
		accessAdapter.setOnItemClickListener(mUserAccessClickListener);
		mUsersWithAccessRecycler.setAdapter(accessAdapter);
		loadUsersWithAccess();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.e(TAG, "onResume");
		updateDataletDisplay(mDatalet);
		loadUsersWithAccess();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_user_info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit_user:
				Intent intent = new Intent(this, DataletFormActivity.class);
				intent.putExtra(DATALET_ID_KEY, mDatalet.getId());
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mViewOnMap) {
			Intent intent = new Intent(this, DataletAccessMapActivity.class);
			intent.putExtra(DATALET_ID_KEY, mDatalet.getId());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mUsersWithAccess.size(); i++) {
				User user = mUsersWithAccess.get(i);
				sb.append(user.getId());
				if (i < mUsersWithAccess.size() - 1) {
					sb.append(",");
				}
			}
			intent.putExtra("accessUsers", sb.toString());

			startActivity(intent);
		}
	}

	private void updateDataletDisplay(Datalet datalet) {
		if (datalet != null) {
			mTitle.setText(datalet.getTitle());
			if (datalet.getDescription() != null) {
				mDescription.setText(datalet.getDescription());
			}
		}
	}

	private void loadUsersWithAccess() {
		DataletsRestClient.getUsersWithAccess(mDatalet.getId(), onUserAccess);
	}

	private DataletsRestClient.ListCallback<User> onUserAccess = new DataletsRestClient.ListCallback<User>() {
		@Override
		public void onSuccess(List<User> list) {
			UsersAdapterHolder adapter = (UsersAdapterHolder) mUsersWithAccessRecycler.getAdapter();
			mUsersWithAccess = list;
			adapter.update(list);
		}

		@Override
		public void onFailure() {
			Log.e(TAG, "Unable to get users with access");
		}
	};

	private OnItemClickListener mUserAccessClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			UsersAdapterHolder adapter = (UsersAdapterHolder) mUsersWithAccessRecycler.getAdapter();
			User user = adapter.getUser(position);
			Intent intent = new Intent(DataletInfoActivity.this, UserInfoActivity.class);
			intent.putExtra(UserFormActivity.USER_ID_KEY, user.getId());
			startActivity(intent);
		}
	};
}
