package edu.utexas.colin.chaacapp.model;


import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.colin.chaacapp.client.DataletsRestClient;
import edu.utexas.colin.chaacapp.events.DataletDeleteEvent;
import edu.utexas.colin.chaacapp.events.DataletSaveEvent;
import edu.utexas.colin.chaacapp.events.UserDeleteEvent;
import edu.utexas.colin.chaacapp.events.UserSaveEvent;
import edu.utexas.colin.chaacapp.model.datalets.Datalet;
import edu.utexas.colin.chaacapp.model.datalets.Datalets;
import edu.utexas.colin.chaacapp.model.users.User;
import edu.utexas.colin.chaacapp.model.users.Users;

public class ChaacModel {

	private static final String TAG = "ChaacModel";
	private static final String USERS_PATH = "users";
	private static final String DATALETS_PATH = "datalets";
	private static ChaacModel _instance;
	private static boolean _loaded = false;
	private static ObjectMapper mapper = new ObjectMapper();

	public static void load() {
		if (!_loaded) {
			_instance = new ChaacModel();
			DataletsRestClient.getAllUsers(onUsersComplete);
			DataletsRestClient.getAllDatalets(onDataletsComplete);
			EventBus.getDefault().register(model());
			_loaded = true;
		}
	}

    public static ChaacModel model() {
        if (_instance == null) {
            _instance = new ChaacModel();
        }

        return _instance;
    }

	private static DataletsRestClient.ListCallback<Datalet> onDataletsComplete = new DataletsRestClient.ListCallback<Datalet>() {
		@Override
		public void onSuccess(List<Datalet> datalets) {
			Log.e(TAG, "Datalets: " + datalets);
			model().addDatalets(datalets);
		}

		@Override
		public void onFailure() {
			Log.e(TAG, "Failed to get datalets");
		}
	};

	private static DataletsRestClient.ListCallback<User> onUsersComplete = new DataletsRestClient.ListCallback<User>() {
		@Override
		public void onSuccess(List<User> users) {
			Log.e(TAG, "Users: " + users);
			model().addUsers(users);
		}

		@Override
		public void onFailure() {
			Log.e(TAG, "Failed to get users");
		}
	};

    private Users mUsers = new Users();
	private Datalets mDatalets = new Datalets();
	private List<UsersListener> mUsersListeners = new ArrayList<>();
	private List<DataletsListener> mDataletsListeners = new ArrayList<>();

	private void addDatalets(List<Datalet> datalets) {
		mDatalets.add(datalets);
		notifyDataletsListeners();
	}

	public void addDatalet(Datalet datalet) {
		mDatalets.add(datalet);
		notifyDataletsListeners();
	}

	public Datalet getDatalet(String dataletID) {
		return mDatalets.get(dataletID);
	}

	public List<Datalet> getDatalets() {
		return mDatalets.getDataletList();
	}

	public List<Datalet> getDataletsOwnedByUser(String userID) {
		return mDatalets.getDataletsOwnedBy(userID);
	}

	public void removeDatalet(String dataletID) {
		mDatalets.removeDatalet(dataletID);
		notifyDataletsListeners();
	}

	public void addDataletsListener(DataletsListener listener) {
		mDataletsListeners.add(listener);
	}

	public void removeDataletsListener(DataletsListener listener){
		mDataletsListeners.remove(listener);
	}

	public void notifyDataletsListeners() {
		for (DataletsListener listener : mDataletsListeners) {
			if (listener != null) {
				listener.notifyDataletsChanged();
			}
		}
	}

    private void addUsers(List<User> users) {
        mUsers.add(users);
		notifyUsersListeners();
    }

    public void addUser(User user) {
        mUsers.add(user);
		notifyUsersListeners();
    }

	public User getUserByEmail(String email) {
		return mUsers.getByEmail(email);
	}

    public User getUser(String userID) {
        return mUsers.get(userID);
    }

	public String getAdminID() {
		return mUsers.getAdminID();
	}

    public List<User> getUsers() {
        return mUsers.getUserList();
    }

    public void removeUser(String userID) {
        mUsers.removeUser(userID);
		notifyUsersListeners();
    }

	public void addUsersListener(UsersListener listener) {
		mUsersListeners.add(listener);
	}

	public void removeUsersListener(UsersListener listener) {
		mUsersListeners.remove(listener);
	}

	public void notifyUsersListeners() {
		for (UsersListener listener : mUsersListeners) {
			if (listener != null) {
				listener.notifyUsersChanged();
			}
		}
	}

	@Subscribe
	public void onEvent(final UserSaveEvent event) {
		User user = event.user;
		if (event.created) {
			DataletsRestClient.createUser(user, new DataletsRestClient.ObjectCallback<User>() {
				@Override
				public void onSuccess(User item) {
					addUser(item);
					Log.e(TAG, "Successfully created user");
					notifyUsersListeners();
				}

				@Override
				public void onFailure() {
					Log.e(TAG, "Failed creating user");
				}
			});
		} else {
			DataletsRestClient.updateUser(user, new DataletsRestClient.ObjectCallback<User>() {
				@Override
				public void onSuccess(User item) {
					Log.e(TAG, "Successfully updated user");
					notifyUsersListeners();
				}

				@Override
				public void onFailure() {
					Log.e(TAG, "Failed updating user");
				}
			});
		}
	}

	@Subscribe
	public void onEvent(final UserDeleteEvent event) {

		DataletsRestClient.deleteUser(event.userID, new DataletsRestClient.ObjectCallback<User>() {
			@Override
			public void onSuccess(User item) {
				Log.e(TAG, "Successfully deleted user");
				notifyUsersListeners();
			}

			@Override
			public void onFailure() {
				Log.e(TAG, "Failed deleting user");
			}
		});
	}

	@Subscribe
	public void onEvent(final DataletSaveEvent event) {
		Datalet datalet = event.datalet;
		if (event.created) {
			DataletsRestClient.createDatalet(datalet, new DataletsRestClient.ObjectCallback<Datalet>() {
				@Override
				public void onSuccess(Datalet item) {
					addDatalet(item);
					Log.e(TAG, "Successfully created datalet");
					notifyUsersListeners();
				}

				@Override
				public void onFailure() {
					Log.e(TAG, "Failed creating datalet");
				}
			});
		} else {
			DataletsRestClient.updateDatalet(datalet, new DataletsRestClient.ObjectCallback<Datalet>() {
			public void onSuccess(Datalet item) {
					Log.e(TAG, "Successfully updated datalet");
					notifyDataletsListeners();
				}

				@Override
				public void onFailure() {
					Log.e(TAG, "Failed updating datalet");
				}
			});
		}
	}

	@Subscribe
	public void onEvent(final DataletDeleteEvent event) {
		DataletsRestClient.deleteDatalet(event.dataletID, new DataletsRestClient.ObjectCallback<Datalet>() {
			@Override
			public void onSuccess(Datalet item) {
				Log.e(TAG, "Successfully deleted datalet");
				notifyUsersListeners();
			}

			@Override
			public void onFailure() {
				Log.e(TAG, "Failed deleting datalet");
			}
		});
	}
}
