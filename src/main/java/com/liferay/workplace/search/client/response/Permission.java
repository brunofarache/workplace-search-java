package com.liferay.workplace.search.client.response;

import java.util.List;

public class Permission {

    public List<String> permissions() {
        return permissions;
    }

    public String user() { return user; }

    private String user;
    private List<String> permissions;

}