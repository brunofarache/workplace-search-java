package com.liferay.workplace.search.client;

import com.google.gson.JsonObject;

public interface Parser {

	JsonObject toJsonObject(Object object) throws Exception;

}