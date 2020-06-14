package com.liferay.workplace.search.client;

import com.google.gson.JsonObject;
import com.liferay.workplace.search.client.mock.Blog;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import com.liferay.workplace.search.client.response.Document;
import com.liferay.workplace.search.client.response.Response;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class IndexDocumentsTest {

    @Test
    public void testIndexDocuments() throws Exception {
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody("{results: [{id: 'abcd', errors: []}]}"));
        server.start();

        HttpUrl url = server.url("/api/ws/v1/sources/[KEY]");

        WorkplaceSearchClient client = new WorkplaceSearchClient.Builder(url, "token").build();

        Response<Document> response = client.indexDocuments(
            Collections.singletonList(new Blog("Bruno Farache", "Document Title"))).execute();

        assertResponse(response);

        RecordedRequest request = server.takeRequest();

        assertEquals("/api/ws/v1/sources/[KEY]/documents/bulk_create", request.getPath());
        assertEquals(request.getHeader("Authorization"), "Bearer token");
        assertEquals(server.getRequestCount(), 1);

        server.shutdown();
    }

    @Test
    public void testIndexDocumentsAsync() throws Exception {
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody("{results: [{id: 'abcd', errors: []}]}"));
        server.start();

        HttpUrl url = server.url("/api/ws/v1/sources/[KEY]");

        WorkplaceSearchClient client = new WorkplaceSearchClient.Builder(url, "token").build();

        CountDownLatch latch = new CountDownLatch(1);

        client.indexDocuments(Collections.singletonList(new Blog("Bruno Farache", "Document Title")))
            .async(
                response -> {
                   assertResponse(response);
                    latch.countDown();
                },
                exception -> {
                    assertNull(exception);
                    latch.countDown();
                }
            );

        latch.await();

        assertEquals(server.getRequestCount(), 1);

        server.shutdown();
    }

    @Test
    public void testUseParser() throws Exception {
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody("{results: [{id: 'abcd', success: true}]}"));
        server.start();

        WorkplaceSearchClient client = new WorkplaceSearchClient.Builder(server.url("/api/ws/v1/sources/[KEY]"), "token")
                .useParser(object -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("custom", "value");

                    return jsonObject;
                })
            .build();

        client.indexDocuments(Collections.singletonList(new Blog("Bruno Farache", "Document Title"))).execute();

        RecordedRequest request = server.takeRequest();

        assertEquals(server.getRequestCount(), 1);
        assertEquals("[{\"custom\":\"value\"}]", request.getBody().readUtf8());

        server.shutdown();
    }

    @Test
    public void testClientConfig() {
        WorkplaceSearchClient client = new WorkplaceSearchClient.Builder(
            "http://localhost:3002", "key", "token").build();

        assertEquals("http://localhost:3002/api/ws/v1/sources/key", client.endpoint());
        assertEquals("token", client.token());
    }

    protected void assertResponse(Response<Document> response) {
        assertTrue(response.isSuccessful());

        Collection<Document> documents = response.body();
        assertEquals(1, documents.size());

        Document document = documents.stream().findFirst().get();
        assertEquals(document.id(), "abcd");
    }

}