package com.liferay.workplace.search.client.mock;

import com.liferay.workplace.search.client.annotation.AllowPermissions;
import com.liferay.workplace.search.client.annotation.DenyPermissions;
import com.liferay.workplace.search.client.annotation.Field;

public class User {

    public User(String firstName, String email) {
        _firstName = firstName;
        _email = email;
    }

    @Field(name = "firstName")
    public String getFirstName() {
        return "overridenFirstName";
    }

    @Field()
    public String email() {
        return _email;
    }

    @DenyPermissions
    public String[] getDenyPermissions() {
        return new String[]{"overridenDenyPermission1"};
    }

    @Field(name = "firstName")
    private String _firstName;

    private String _email;

    @AllowPermissions
    private String[] _allowPermmissions = new String[]{"allowPermission1"};

    @DenyPermissions
    private String[] _denyPermmissions = new String[]{"denyPermission1"};

}
