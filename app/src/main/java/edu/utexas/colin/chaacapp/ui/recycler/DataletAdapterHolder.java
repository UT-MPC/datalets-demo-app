package edu.utexas.colin.chaacapp.ui.recycler;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.model.datalets.Datalet;

public class DataletAdapterHolder extends RecyclerView.Adapter<DataletAdapterHolder.DataletViewHolder> {

	private List<Datalet> mDatalets = new ArrayList<>();
	private Context mContext;
	private OnItemClickListener mItemClickListener;

	public DataletAdapterHolder(List<Datalet> datalets, Context context) {
		mDatalets = datalets;
		mContext = context;
	}

	@Override
	public DataletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.datalet_item_view, parent, false);
		return new DataletViewHolder(view);
	}

	@Override
	public void onBindViewHolder(DataletViewHolder holder, int position) {
		Datalet datalet = mDatalets.get(position);
		holder.bindDatalet(datalet);
	}

	@Override
	public int getItemCount() {
		return mDatalets.size();
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	public Datalet getDatalet(int position) {
		if (position < 0 || position >= getItemCount()) {
			return null;
		} else {
			return mDatalets.get(position);
		}
	}

	public void update(List<Datalet> datalets) {
		mDatalets.clear();
		mDatalets.addAll(datalets);
		notifyDataSetChanged();
	}

	public void setOnItemClickListener(final OnItemClickListener listener) {
		this.mItemClickListener = listener;
	}

	class DataletViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		@BindView(R.id.datalet_card_view)
		CardView mCardView;

		@BindView(R.id.datalet_title)
		TextView mTitle;

		@BindView(R.id.datalet_description)
		TextView mDescription;

		private Datalet mDatalet;
		private View mView;

		DataletViewHolder(View itemView) {
			super(itemView);

			mView = itemView;

			itemView.setOnClickListener(this);
			itemView.setClickable(true);

			ButterKnife.bind(this, itemView);
		}

		void bindDatalet(Datalet datalet) {
			mDatalet = datalet;
			mTitle.setText(mDatalet.getTitle());
			if (mDatalet.getDescription() != null) {
				mDescription.setText(mDatalet.getDescription());
			} else {
				mDescription.setVisibility(View.GONE);
			}
		}

		@Override
		public void onClick(View v) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(v, getAdapterPosition());
			}
		}
	}
}

