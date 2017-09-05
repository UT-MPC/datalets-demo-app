package edu.utexas.colin.chaacapp.model.users;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Users {

    private Map<String, User> mUsers = new HashMap<>();
    private User mAdmin = new User();

    public List<User> getUserList() {
        return new ArrayList<>(mUsers.values());
    }

    public void add(List<User> users) {
        for (User u : users) {
            if (u.getId() != null) {
				if ("admin@gmail.com".equals(u.getEmail())) {
					mAdmin = u;
				} else {
					mUsers.put(u.getId(), u);
				}
            }
        }
    }

    public void add(User user) {
        add(Collections.singletonList(user));
    }

    public User get(String userID) {
        return mUsers.get(userID);
    }

	public String getAdminID() {
		return mAdmin.getId();
	}

    public User getByEmail(String email) {
        for (User u : mUsers.values()) {
			if (email.equals(u.getEmail())) {
				return u;
			}
		}

		return null;
    }

    public void removeUser(String userID) {
        mUsers.remove(userID);
    }
}
