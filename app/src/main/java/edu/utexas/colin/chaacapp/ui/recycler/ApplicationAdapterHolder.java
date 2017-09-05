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
import edu.utexas.colin.chaacapp.model.users.ApplicationItem;

public class ApplicationAdapterHolder extends RecyclerView.Adapter<ApplicationAdapterHolder.ApplicationViewHolder>  {

	private List<ApplicationItem> mItems = new ArrayList<>();
	private OnItemClickListener mItemClickListener;

	public ApplicationAdapterHolder(List<ApplicationItem> items) {
		mItems = items;
	}

	@Override
	public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_item_view, parent, false);
		return new ApplicationViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ApplicationViewHolder holder, int position) {
		ApplicationItem item = mItems.get(position);
		holder.bindApplicationItem(item);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	public ApplicationItem getItem(int position) {
		if (position < 0 || position >= getItemCount()) {
			return null;
		} else {
			return mItems.get(position);
		}
	}

	public List<ApplicationItem> getItems() {
		return mItems;
	}

	public void update(List<ApplicationItem> items) {
		mItems.clear();
		mItems.addAll(items);
		notifyDataSetChanged();
	}

	public void addItem(ApplicationItem item) {
		mItems.add(item);
		notifyItemInserted(mItems.size() - 1);
	}

	public void removeItem(int position) {
		mItems.remove(position);
		notifyItemRemoved(position);
		notifyItemRangeChanged(position, mItems.size());
	}

	public void setOnItemClickListener(final OnItemClickListener listener) {
		this.mItemClickListener = listener;
	}
	class ApplicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		@BindView(R.id.application_card_view)
		CardView mCardView;

		@BindView(R.id.application_key)
		TextView mKey;

		@BindView(R.id.application_value)
		TextView mValue;

		private ApplicationItem mItem;
		private View mView;

		ApplicationViewHolder(View itemView) {
			super(itemView);

			mView = itemView;

			itemView.setOnClickListener(this);
			itemView.setClickable(true);

			ButterKnife.bind(this, itemView);
		}

		void bindApplicationItem(ApplicationItem item) {
			mItem = item;
			mKey.setText(mItem.getKey());
			mValue.setText(mItem.getValue());
		}

		@Override
		public void onClick(View v) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(v, getAdapterPosition());
			}
		}
	}
}
