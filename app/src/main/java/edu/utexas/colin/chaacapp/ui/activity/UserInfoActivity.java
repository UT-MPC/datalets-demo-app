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
import edu.utexas.colin.chaacapp.ui.recycler.DataletAdapterHolder;
import edu.utexas.colin.chaacapp.ui.recycler.OnItemClickListener;


public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

	public static final String TAG = "UserInfoActivity";

	private User mUser;
	private List<Datalet> mAvailableDatalets = new ArrayList<>();

	@BindView(R.id.user_info_name)
	TextView mUserName;

	@BindView(R.id.user_info_email)
	TextView mUserEmail;

	@BindView(R.id.user_info_available_datalets)
	RecyclerView mAvailableRecycler;

	@BindView(R.id.user_view_on_map)
	Button mViewOnMap;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_user_info;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String userID = intent.getStringExtra(UserFormActivity.USER_ID_KEY);
		getSupportActionBar().setElevation(0);
		mUser = ChaacModel.model().getUser(userID);
		updateUserDisplay(mUser);

		mViewOnMap.setOnClickListener(this);

		mAvailableRecycler.setLayoutManager(new LinearLayoutManager(this));
		DataletAdapterHolder availableAdapter = new DataletAdapterHolder(new ArrayList<>(), this);
		mAvailableRecycler.setAdapter(availableAdapter);
		loadAvailableDatalets();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.e(TAG, "onResume");
		updateUserDisplay(mUser);
		loadAvailableDatalets();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_user_info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case R.id.action_edit_user:
				intent = new Intent(this, UserFormActivity.class);
				intent.putExtra(UserFormActivity.USER_ID_KEY, mUser.getId());
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public void onClick(View v) {
		if (v == mViewOnMap) {
			Intent intent = new Intent(this, UserAccessMapActivity.class);
			intent.putExtra(UserFormActivity.USER_ID_KEY, mUser.getId());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mAvailableDatalets.size(); i++) {
				Datalet datalet = mAvailableDatalets.get(i);
				sb.append(datalet.getId());
				if (i < mAvailableDatalets.size() - 1) {
					sb.append(",");
				}
			}
			intent.putExtra("availableDatalets", sb.toString());

			startActivity(intent);
		}
	}

	private void loadAvailableDatalets() {
		DataletsRestClient.getAvailableDatalets(mUser.getId(), onAvailableDatalets);
	}

	private void updateUserDisplay(User user) {
		if (user != null) {
			mUserName.setText(user.getFullName(false));
			mUserEmail.setText(user.getEmail());
		}
	}

	private DataletsRestClient.ListCallback<Datalet> onAvailableDatalets = new DataletsRestClient.ListCallback<Datalet>() {
		@Override
		public void onSuccess(List<Datalet> datalets) {
			DataletAdapterHolder adapter = (DataletAdapterHolder) mAvailableRecycler.getAdapter();
			mAvailableDatalets = datalets;
			adapter.update(datalets);
		}

		@Override
		public void onFailure() {
			Log.e(TAG, "Unable to get available datalets");
		}
	};

	private OnItemClickListener mAvailableClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			DataletAdapterHolder adapter = (DataletAdapterHolder) mAvailableRecycler.getAdapter();
			Datalet datalet = adapter.getDatalet(position);
			Intent intent = new Intent(UserInfoActivity.this, DataletInfoActivity.class);
			intent.putExtra(DataletInfoActivity.DATALET_ID_KEY, datalet.getId());
			startActivity(intent);
		}
	};
}
