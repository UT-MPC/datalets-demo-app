package edu.utexas.colin.chaacapp.model.users;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utexas.colin.chaacapp.model.shared.GPSPoint;
import edu.utexas.colin.chaacapp.model.shared.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String deviceID;
    private String gender;
    private LocalDate birthdate;
    private GPSPoint location;
    private List<String> groups;
    private Map<String, String> application;

    public User() {
    }

    @JsonIgnore
    public String getFullName(boolean lastFirst) {
        if (lastFirst) {
            return lastName + ", " + firstName;
        } else {
            return firstName + " " + lastName;
        }
    }

    @JsonIgnore
    public boolean isValid() {
        return firstName == null || lastName == null || email == null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public GPSPoint getLocation() {
        return location;
    }

    public void setLocation(GPSPoint location) {
        this.location = location;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public Map<String, String> getApplication() {
        return application;
    }

    public void setApplication(Map<String, String> application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", deviceID='" + deviceID + '\'' +
                ", gender='" + gender + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", location=" + location +
                ", groups=" + groups +
                ", application=" + application +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (deviceID != null ? !deviceID.equals(user.deviceID) : user.deviceID != null) return false;
        if (gender != null ? !gender.equals(user.gender) : user.gender != null) return false;
        if (birthdate != null ? !birthdate.equals(user.birthdate) : user.birthdate != null) return false;
        if (location != null ? !location.equals(user.location) : user.location != null) return false;
        if (groups != null ? !groups.equals(user.groups) : user.groups != null) return false;
        return application != null ? application.equals(user.application) : user.application == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (deviceID != null ? deviceID.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (birthdate != null ? birthdate.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        result = 31 * result + (application != null ? application.hashCode() : 0);
        return result;
    }
}
