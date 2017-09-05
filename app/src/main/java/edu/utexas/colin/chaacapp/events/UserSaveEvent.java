package edu.utexas.colin.chaacapp.events;


import edu.utexas.colin.chaacapp.model.users.User;

public class UserSaveEvent {
    public User user;
    public boolean created = false;
}
