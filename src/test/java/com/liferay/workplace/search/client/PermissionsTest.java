package com.liferay.workplace.search.client;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import com.liferay.workplace.search.client.response.Permission;
import com.liferay.workplace.search.client.response.Response;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class PermissionsTest {

    @Test
    public void testListAllPermissions() throws Exception {
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody(
            "{'meta': {'page': {'current': 1, 'total_pages': 1, 'total_results': 1, 'size': 25}}," +
            "'results':[{'user': 'enterprise_search', 'permissions': ['permission1']}]}"));

        server.start();

        HttpUrl url = server.url("/api/ws/v1/sources/[KEY]");

        WorkplaceSearchClient client = new WorkplaceSearchClient.Builder(url, "token").build();

        Response<Permission> response = client.listAllPermissions(-1, -1).execute();

        assertResponse(response);

        RecordedRequest request = server.takeRequest();

        assertEquals("/api/ws/v1/sources/[KEY]/permissions", request.getPath());
        assertEquals(request.getHeader("Authorization"), "Bearer token");
        assertEquals(server.getRequestCount(), 1);

        server.shutdown();
    }

    @Test
    public void testListAllPermissionsAsync() throws Exception {
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody(
            "{'meta': {'page': {'current': 1, 'total_pages': 1, 'total_results': 1, 'size': 25}}," +
            "'results':[{'user': 'enterprise_search', 'permissions': ['permission1']}]}"));

        server.start();

        HttpUrl url = server.url("/api/ws/v1/sources/[KEY]");

        WorkplaceSearchClient client = new WorkplaceSearchClient.Builder(url, "token").build();

        CountDownLatch latch = new CountDownLatch(1);

        client.listAllPermissions(-1, -1)
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


    protected void assertResponse(Response<Permission> response) {
        assertTrue(response.isSuccessful());

        Collection<Permission> permissions = response.body();
        assertEquals(1, permissions.size());

        Permission permission = permissions.stream().findFirst().get();

        assertEquals(permission.user(), "enterprise_search");
        assertEquals(permission.permissions().get(0), "permission1");
    }

}