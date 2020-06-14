package com.liferay.workplace.search.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.liferay.workplace.search.client.annotation.AnnotationParser;
import okhttp3.HttpUrl;
import com.liferay.workplace.search.client.response.Document;
import com.liferay.workplace.search.client.response.Permission;

import java.util.List;
import java.util.Objects;

public class WorkplaceSearchClient {

	private WorkplaceSearchClient(Builder builder) {
		_endpoint = builder.endpoint;
		_gson = new Gson();
		_parser = (builder.parser == null) ? new AnnotationParser() : builder.parser;
		_token = builder.token;
	}

	public String endpoint() {
		return _endpoint;
	}

	public String token() {
		return _token;
	}

	public Call<Document> indexDocuments(String documentsJsonArray) {
		return new Call<>(documentsJsonArray, Document.class, _endpoint + _CREATE_PATH, _token);
	}

	public Call<Document> indexDocuments(List<Object> documents) throws Exception {
		return indexDocuments(toJsonArray(documents));
	}

	public Call<Document> destroyDocuments(String idsJsonArray) {
		return new Call<>(idsJsonArray, Document.class, _endpoint + _DESTROY_PATH, _token);
	}

	public Call<Document> destroyDocuments(List<String> ids) {
		return destroyDocuments(_gson.toJson(ids));
	}

	public Call<Permission> listAllPermissions(int currentPage, int pageSize) {
		HttpUrl.Builder builder = HttpUrl.parse(_endpoint + _PERMISSIONS_PATH).newBuilder();

		if (currentPage >= 0) {
			builder.addQueryParameter("page[current]", String.valueOf(currentPage));
		}

		if (pageSize >= 0) {
			builder.addQueryParameter("page[size]", String.valueOf(pageSize));
		}

		return new Call<>(Permission.class, builder.toString(), _token);
	}

	protected String toJsonArray(List<Object> documents) throws Exception {
		JsonArray jsonArray = new JsonArray();

		for (Object document : documents) {
			jsonArray.add(_parser.toJsonObject(document));
		}

		return jsonArray.toString();
	}

	public static class Builder {

		public Builder(HttpUrl endpoint, String token) {
			this.endpoint = endpoint.toString();
			this.token = token;
		}

		public Builder(String host, String key, String token) {
			this(Objects.requireNonNull(HttpUrl.parse(String.format("%s/api/ws/v1/sources/%s", host, key))), token);
		}

		public WorkplaceSearchClient build() {
			return new WorkplaceSearchClient(this);
		}

		public Builder useParser(Parser parser) {
			this.parser = parser;
			return this;
		}

		private String endpoint;
		private Parser parser;
		private String token;

	}

	private static final String _CREATE_PATH = "/documents/bulk_create";
	private static final String _DESTROY_PATH = "/documents/bulk_destroy";
	private static final String _PERMISSIONS_PATH = "/permissions";

	private final String _endpoint;
	private final Gson _gson;
	private final Parser _parser;
	private final String _token;

}