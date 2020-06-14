package com.liferay.workplace.search.client.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

public class Response<T> {

    public Response(okhttp3.Response response, Class<T> clazz) {
        if (clazz == Document.class) {
            _collectionType = new TypeToken<Collection<Document>>(){}.getType();
        }
        else if (clazz == Permission.class) {
            _collectionType = new TypeToken<Collection<Permission>>(){}.getType();
        }

        _response = response;
    }

    public String bodyString() {
        if (_body == null) {
            try {
                _body = _response.body().string();
            }
            catch (IOException ioe) {
            }
        }

        return _body;
    }

    public Collection<T> body() {
        Results results = _gson.fromJson(bodyString(), Results.class);
        return _gson.fromJson(results.results(), _collectionType);
    }

    public boolean isSuccessful() {
        return _response.isSuccessful();
    }

    private String _body;
    private Type _collectionType;
    private Gson _gson = new Gson();
    private okhttp3.Response _response;

}
