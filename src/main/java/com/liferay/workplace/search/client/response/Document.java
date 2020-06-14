package com.liferay.workplace.search.client.response;

import java.util.Collection;

public class Document {

    public Collection<String> errors() {
        return errors;
    };

    public String id() {
        return id;
    }

    public boolean isSuccess() {
        return success;
    }

    private Collection<String> errors;
    private String id;
    private boolean success;

}