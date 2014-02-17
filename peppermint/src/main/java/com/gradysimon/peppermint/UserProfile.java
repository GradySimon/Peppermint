package com.gradysimon.peppermint;

/**
 * Created by grady on 2/14/14.
 */
public class UserProfile {
    private int id;
    private String firstName;
    private String lastName;

    UserProfile(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserProfile() {
        // no-args constructor for gson
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getWholeName() {
        return getFirstName() + " " + getLastName();
    }
}
