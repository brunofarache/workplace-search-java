package com.liferay.workplace.search.client.mock;

import com.liferay.workplace.search.client.annotation.AllowPermissions;
import com.liferay.workplace.search.client.annotation.DenyPermissions;
import com.liferay.workplace.search.client.annotation.Field;

public class Blog {

    public Blog(String author, String title) {
        this.author = author;
        _title = title;
    }

    @AllowPermissions
    public String[] getAllowPermissions() {
        return new String[]{"allowPermission1", "allowPermission2"};
    }

    public String getAuthor() {
        return author;
    }

    @DenyPermissions
    public String[] getDenyPermissions() {
        return new String[]{"denyPermission1", "denyPermission2"};
    }

    public String getTitle() {
        return _title;
    }

    @Field
    private String author;

    @Field(name = "title")
    private String _title;

    private String _secret = "this is a secret";

}
