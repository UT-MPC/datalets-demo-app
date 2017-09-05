package edu.utexas.colin.chaacapp.ui.recycler;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.model.users.User;

public class UsersAdapterHolder extends RecyclerView.Adapter<UsersAdapterHolder.UsersViewHolder> {

	private List<User> mUsers = new ArrayList<>();
	private Context mContext;
	private OnItemClickListener mItemClickListener;

	public UsersAdapterHolder(List<User> users, Context context) {
		mUsers = users;
		mContext = context;
	}

	@Override
	public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item_view, parent, false);
		return new UsersViewHolder(view);
	}

	@Override
	public void onBindViewHolder(UsersViewHolder holder, int position) {
		User user = mUsers.get(position);
		holder.bindUser(user);
	}

	@Override
	public int getItemCount() {
		return mUsers.size();
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	public User getUser(int position) {
		if (position < 0 || position >= getItemCount()) {
			return null;
		} else {
			return mUsers.get(position);
		}
	}

	public void update(List<User> users) {
		mUsers.clear();
		mUsers.addAll(users);
		notifyDataSetChanged();
	}

	public void setOnItemClickListener(final OnItemClickListener listener) {
		this.mItemClickListener = listener;
	}

	class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		@BindView(R.id.user_card_view)
		CardView mCardView;

		@BindView(R.id.user_name)
		TextView mName;

		@BindView(R.id.user_email)
		TextView mEmail;

		@BindView(R.id.user_image)
		ImageView mImageView;

		private User mUser;
		private View mView;

		UsersViewHolder(View itemView) {
			super(itemView);

			mView = itemView;

			itemView.setOnClickListener(this);
			itemView.setClickable(true);

			ButterKnife.bind(this, itemView);
		}

		void bindUser(User user) {
			mUser = user;
			mName.setText(mUser.getFullName(false));
			mEmail.setText(mUser.getEmail());
			mImageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
		}

		@Override
		public void onClick(View v) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(v, getAdapterPosition());
			}
		}
	}
}
