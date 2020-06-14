package com.liferay.workplace.search.client;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import com.liferay.workplace.search.client.response.Document;
import com.liferay.workplace.search.client.response.Response;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class DestroyDocumentsTest {

    @Test
    public void testDestroyDocuments() throws Exception {
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody("{results: [{id: 'abcd', success: true}]}"));
        server.start();

        HttpUrl url = server.url("/api/ws/v1/sources/[KEY]");

        WorkplaceSearchClient client = new WorkplaceSearchClient.Builder(url, "token").build();

        Response<Document> response = client.destroyDocuments(Collections.singletonList("abcd")).execute();

        assertResponse(response);

        RecordedRequest request = server.takeRequest();

        assertEquals("/api/ws/v1/sources/[KEY]/documents/bulk_destroy", request.getPath());
        assertEquals(request.getHeader("Authorization"), "Bearer token");
        assertEquals(server.getRequestCount(), 1);

        server.shutdown();
    }

    @Test
    public void testDestroyDocumentsAsync() throws Exception {
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody("{results: [{id: 'abcd', success: true}]}"));
        server.start();

        HttpUrl url = server.url("/api/ws/v1/sources/[KEY]");

        WorkplaceSearchClient client = new WorkplaceSearchClient.Builder(url, "token").build();

        CountDownLatch latch = new CountDownLatch(1);

        client.destroyDocuments(Collections.singletonList("abcd"))
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

    protected void assertResponse(Response<Document> response) {
        assertTrue(response.isSuccessful());

        Collection<Document> documents = response.body();
        assertEquals(1, documents.size());

        Document document = documents.stream().findFirst().get();

        assertEquals(document.id(), "abcd");
        assertTrue(document.isSuccess());
    }

}