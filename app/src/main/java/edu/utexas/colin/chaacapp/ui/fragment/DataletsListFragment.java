package edu.utexas.colin.chaacapp.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.model.ChaacModel;
import edu.utexas.colin.chaacapp.model.DataletsListener;
import edu.utexas.colin.chaacapp.model.datalets.Datalet;
import edu.utexas.colin.chaacapp.ui.activity.DataletFormActivity;
import edu.utexas.colin.chaacapp.ui.activity.DataletInfoActivity;
import edu.utexas.colin.chaacapp.ui.recycler.DataletAdapterHolder;
import edu.utexas.colin.chaacapp.ui.recycler.OnItemClickListener;

public class DataletsListFragment extends BaseFragment implements DataletsListener, View.OnClickListener {

	private static final String TAG = "DataletsListActivity";

	@BindView(R.id.datalets_recycler_view)
	RecyclerView mRecycler;
//	@BindView(R.id.add_datalet_button)
//	FloatingActionButton mAddDataletButton;

	public DataletsListFragment() {

	}

	@Override
	protected int getLayoutResID() {
		return R.layout.fragment_datalets_list;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		DataletAdapterHolder adapter = new DataletAdapterHolder(ChaacModel.model().getDatalets(), getActivity());
		adapter.setOnItemClickListener(mItemClickListener);
		mRecycler.setAdapter(adapter);

//		mAddDataletButton.setOnClickListener(this);
		ChaacModel.model().addDataletsListener(this);

		return view;
	}

	@Override
	public void onDestroyView() {
		ChaacModel.model().removeDataletsListener(this);
		super.onDestroyView();
	}

	@Override
	public void onClick(View view) {
//		if (view == mAddDataletButton) {
//			addDataletClicked();
//		}
	}

	@Override
	public void notifyDataletsChanged() {
		DataletAdapterHolder adapter = (DataletAdapterHolder) mRecycler.getAdapter();
		adapter.update(ChaacModel.model().getDatalets());
	}

	private void addDataletClicked() {
		Intent intent = new Intent(getActivity(), DataletFormActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			DataletAdapterHolder adapter = (DataletAdapterHolder) mRecycler.getAdapter();
			Datalet datalet = adapter.getDatalet(position);

			Intent intent = new Intent(mView.getContext(), DataletInfoActivity.class);
			intent.putExtra(DataletInfoActivity.DATALET_ID_KEY, datalet.getId());
			mView.getContext().startActivity(intent);
		}
	};
}
