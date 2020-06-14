package com.liferay.workplace.search.client;

import okhttp3.*;
import com.liferay.workplace.search.client.response.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

public class Call<T> {

	public Call(String body,  Class<T> clazz, String url,  String token) {
		_body = body;
		_clazz = clazz;
		_client = new OkHttpClient();
		_url = url;
		_token = token;
	}

	public Call(Class<T> clazz, String url,  String token) {
		_body = null;
		_clazz = clazz;
		_client = new OkHttpClient();
		_url = url;
		_token = token;
	}

	 public Response<T> execute() throws  Exception {
		 Request request = (_body == null) ? createGetRequest() : createPostRequest(_body);
		 return new Response<>(_client.newCall(request).execute(), _clazz);
	 }

	 public void async(Consumer<Response<T>> onResponse, Consumer<Exception> onFailure) {
		 Request request = (_body == null) ? createGetRequest() : createPostRequest(_body);

		 _client.newCall(request).enqueue(new Callback() {
			 @Override
			 public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
				 onFailure.accept(e);
			 }

			 @Override
			 public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) {
				 onResponse.accept(new Response<>(response, _clazz));
			 }
		 });
	 }

	protected Request createGetRequest() {
		Request.Builder builder = new Request.Builder()
			.url(_url)
			.get();

		return addAuthorization(builder).build();
	}

	protected Request createPostRequest(String body) {
		Request.Builder builder = new Request.Builder()
			.url(_url)
			.post(RequestBody.create(body, _JSON));

		return addAuthorization(builder).build();
	}

	protected Request.Builder addAuthorization(Request.Builder builder) {
		return builder.header("Authorization", "Bearer " + _token);
	}

	private final MediaType _JSON = MediaType.get("application/json; charset=utf-8");

	private final String _body;
	private final Class<T> _clazz;
	private final OkHttpClient _client;

	private final String _token;
	private final String _url;

}