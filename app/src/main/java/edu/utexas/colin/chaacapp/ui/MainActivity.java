package edu.utexas.colin.chaacapp.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.model.ChaacModel;
import edu.utexas.colin.chaacapp.ui.activity.DataletFormActivity;
import edu.utexas.colin.chaacapp.ui.activity.UserFormActivity;
import edu.utexas.colin.chaacapp.ui.fragment.DataletsListFragment;
import edu.utexas.colin.chaacapp.ui.fragment.UsersListFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {

	private static final String BACKGROUND_URL = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
	private static final String PROFILE_URL = "https://www.gravatar.com/avatar/62590dfbcfab942b950ed95924bf93a6.jpg";

	private static final String TAG = "MainActivity";
	private static final String TAG_USERS = "users";
	private static final String TAG_DATALETS = "datalets";

	public static String CURRENT_TAG = TAG_USERS;

	public static int mNavItemIndex = 0;

	@BindView(R.id.nav_view)
	NavigationView mNavigationView;

	@BindView(R.id.drawer_layout)
	DrawerLayout mDrawer;

	@BindView(R.id.fab)
	FloatingActionButton mFab;

	@BindView(R.id.toolbar)
	Toolbar mToolbar;

	private View mNavHeader;
	private ImageView mImgHeaderBg, mImgProfile;
	private TextView mProfileName, mProfileWebsite;

	private String[] mActivityTitles;

	private Handler mHandler;

//	ListView mDrawerList;
//	RelativeLayout mDrawerPane;
//	private ActionBarDrawerToggle mDrawerToggle;
//	private DrawerLayout mDrawerLayout;
//
//	List<NavItem> mNavItems = new ArrayList<>();

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_main;
	}

	@Override
	protected boolean makeTranslucent() {
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);

		ChaacModel.load();

		setSupportActionBar(mToolbar);

		mHandler = new Handler();

		mNavHeader = mNavigationView.getHeaderView(0);
		mProfileName = (TextView) mNavHeader.findViewById(R.id.name);
		mProfileWebsite = (TextView) mNavHeader.findViewById(R.id.website);
		mImgHeaderBg = (ImageView) mNavHeader.findViewById(R.id.img_header_bg);
		mImgProfile = (ImageView) mNavHeader.findViewById(R.id.img_profile);

		mActivityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

		mFab.setOnClickListener(this);

		loadNavHeader();

		setupNavigationView();

		if (savedInstanceState == null) {
			mNavItemIndex = 0;
			CURRENT_TAG = TAG_USERS;
			loadHomeFragment();
		}
	}

	private void loadNavHeader() {
		mProfileName.setText("Colin Maxfield");
		mProfileWebsite.setText("http://mpc.ece.utexas.edu/");

		// loading header background image
		Glide.with(this).load(BACKGROUND_URL)
				.crossFade()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(mImgHeaderBg);

		// Loading profile image
		Glide.with(this).load(PROFILE_URL)
				.crossFade()
				.thumbnail(0.5f)
				.bitmapTransform(new CircleTransform(this))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(mImgProfile);
	}

	private void loadHomeFragment() {
		selectNavMenu();

		setToolbarTitle();

		if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
			mDrawer.closeDrawers();

			return;
		}

		Runnable pendingRunnable = new Runnable() {
			@Override
			public void run() {
				Fragment fragment = getHomeFragment();
				FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
				fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
						android.R.anim.fade_out);
				fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
				fragmentTransaction.commitAllowingStateLoss();
			}
		};

		mHandler.post(pendingRunnable);

		mDrawer.closeDrawers();

		invalidateOptionsMenu();
	}

	private Fragment getHomeFragment() {
		switch (mNavItemIndex) {
			case 1:
				DataletsListFragment dataletsListFragment = new DataletsListFragment();
				return dataletsListFragment;
			case 0: // Fall through intentional
			default:
				UsersListFragment usersListFragment = new UsersListFragment();
				return usersListFragment;
		}
	}

	private void setToolbarTitle() {
		getSupportActionBar().setTitle(mActivityTitles[mNavItemIndex]);
	}

	private void selectNavMenu() {
		mNavigationView.getMenu().getItem(mNavItemIndex).setChecked(true);
	}

	private void setupNavigationView() {
		mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.nav_admin:
						mNavItemIndex = 1;
						CURRENT_TAG = TAG_DATALETS;
						break;
					case R.id.nav_players: // Fall through intentional
					default:
						mNavItemIndex = 0;
						CURRENT_TAG = TAG_USERS;
						break;
				}

				if (item.isChecked()) {
					item.setChecked(false);
				} else {
					item.setChecked(true);
				}
				item.setChecked(true);

				loadHomeFragment();

				return true;
			}
		});

		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View drawerView) {
				// Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
				super.onDrawerOpened(drawerView);
			}
		};

		//Setting the actionbarToggle to drawer layout
		mDrawer.addDrawerListener(actionBarDrawerToggle);

		//calling sync state is necessary or else your hamburger icon wont show up
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void onClick(View v) {
		if (v == mFab) {
			switch (mNavItemIndex) {
				case 0:
					addUserClicked();
					break;
				case 1:
					addDataletClicked();
					break;
			}
		}
	}

	private void addUserClicked() {
		Intent intent = new Intent(this, UserFormActivity.class);
		startActivity(intent);
	}

	private void addDataletClicked() {
		Intent intent = new Intent(this, DataletFormActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		if (mDrawer.isDrawerOpen(GravityCompat.START)) {
			mDrawer.closeDrawers();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mNavItemIndex == 0) {
			//TODO: handle different menus
		} else if (mNavItemIndex == 1) {

		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

//		if (mDrawerToggle.onOptionsItemSelected(item)) {
//			return true;
//		}

		return super.onOptionsItemSelected(item);
	}

//	@Override
//	protected void onPostCreate(Bundle savedInstanceState) {
//		super.onPostCreate(savedInstanceState);
//		mDrawerToggle.syncState();
//	}
//
//	private void selectItemFromDrawer(int position) {
//		Fragment fragment;
//		switch (position) {
//			case 1:
//				fragment = new DataletsListFragment();
//				break;
//			case 0:
//			default:
//				fragment = new UsersListFragment();
//		}
//
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		fragmentManager.beginTransaction()
//				.replace(R.id.mainContent, fragment)
//				.commit();
//
//		mDrawerList.setItemChecked(position, true);
//		setTitle(mNavItems.get(position).mTitle);
//
//		mDrawerLayout.closeDrawer(mDrawerPane);
//	}
//
//	private void prepareNavItems() {
//		if (mNavItems.size() == 0) {
//			mNavItems.add(new NavItem("Users", "View current users", R.drawable.ic_people_black_24dp));
//			mNavItems.add(new NavItem("Datalets", "View all datalets", R.drawable.ic_pin_drop_black_24dp));
//		}
//	}
//
//	class NavItem {
//		String mTitle;
//		String mSubtitle;
//		int mIcon;
//
//		public NavItem(String title, String subtitle, int icon) {
//			mTitle = title;
//			mSubtitle = subtitle;
//			mIcon = icon;
//		}
//	}
//
//	class DrawerListAdapter extends BaseAdapter {
//
//		Context mContext;
//		List<NavItem> mNavItems;
//
//		public DrawerListAdapter(Context context, List<NavItem> navItems) {
//			mContext = context;
//			mNavItems = navItems;
//		}
//
//		@Override
//		public int getCount() {
//			return mNavItems.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return mNavItems.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return 0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view;
//
//			if (convertView == null) {
//				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				view = inflater.inflate(R.layout.drawer_item, null);
//			}
//			else {
//				view = convertView;
//			}
//
//			TextView titleView = (TextView) view.findViewById(R.id.title);
//			TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
//			ImageView iconView = (ImageView) view.findViewById(R.id.icon);
//
//			titleView.setText( mNavItems.get(position).mTitle );
//			subtitleView.setText( mNavItems.get(position).mSubtitle );
//			iconView.setImageResource(mNavItems.get(position).mIcon);
//
//			return view;
//		}
//	}
}
