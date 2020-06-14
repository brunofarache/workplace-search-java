package com.liferay.workplace.search.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.liferay.workplace.search.client.annotation.AnnotationParser;
import com.liferay.workplace.search.client.mock.Blog;
import com.liferay.workplace.search.client.mock.User;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class AnnotationParserTest {

    @Test
    public void testFieldAnnotation() throws Exception {
        Parser parser = new AnnotationParser();

        Blog blog = new Blog("Bruno Farache", "Document Title");

        JsonObject jsonObject = parser.toJsonObject(blog);

        assertEquals(blog.getAuthor(), jsonObject.get("author").getAsString());
        assertEquals(blog.getTitle(), jsonObject.get("title").getAsString());
        assertNull(jsonObject.get("secret"));
    }

    @Test
    public void testFieldAnnotationAsMethod() throws Exception {
        Parser parser = new AnnotationParser();

        User user = new User("Bruno Farache", "xyz@gmail.com");

        JsonObject jsonObject = parser.toJsonObject(user);

        String actual = jsonObject.get("firstName").getAsString();

        assertEquals("overridenFirstName", actual);
        assertEquals(user.getFirstName(), actual);
        assertEquals(user.email(), jsonObject.get("email").getAsString());
    }

    @Test
    public void testAllowPermissionsAnnotation() throws Exception {
        Parser parser = new AnnotationParser();

        Blog blog = new Blog("Bruno Farache", "Document Title");

        JsonObject jsonObject = parser.toJsonObject(blog);

        JsonArray allowPermissions = jsonObject.get("_allow_permissions").getAsJsonArray();
        assertEquals(2, allowPermissions.size());

        Iterator<JsonElement> iterator = allowPermissions.iterator();

        JsonElement jsonElement = iterator.next();
        assertEquals("allowPermission1", jsonElement.getAsString());

        jsonElement = iterator.next();
        assertEquals("allowPermission2", jsonElement.getAsString());
    }

    @Test
    public void testAllowPermissionsAnnotationAsField() throws Exception {
        Parser parser = new AnnotationParser();

        User user = new User("Bruno Farache", "xyz@gmail.com");

        JsonObject jsonObject = parser.toJsonObject(user);

        JsonArray allowPermissions = jsonObject.get("_allow_permissions").getAsJsonArray();
        assertEquals(1, allowPermissions.size());

        Iterator<JsonElement> iterator = allowPermissions.iterator();

        JsonElement jsonElement = iterator.next();
        assertEquals("allowPermission1", jsonElement.getAsString());

        JsonArray denyPermissions = jsonObject.get("_deny_permissions").getAsJsonArray();
        assertEquals(1, allowPermissions.size());

        iterator = denyPermissions.iterator();

        jsonElement = iterator.next();
        assertEquals("overridenDenyPermission1", jsonElement.getAsString());
    }

    @Test
    public void testDenyPermissionsAnnotation() throws Exception {
        Parser parser = new AnnotationParser();

        Blog blog = new Blog("Bruno Farache", "Document Title");

        JsonObject jsonObject = parser.toJsonObject(blog);

        JsonArray denyPermissions = jsonObject.get("_deny_permissions").getAsJsonArray();
        assertEquals(2, denyPermissions.size());

        Iterator<JsonElement> iterator = denyPermissions.iterator();

        JsonElement jsonElement = iterator.next();
        assertEquals("denyPermission1", jsonElement.getAsString());

        jsonElement = iterator.next();
        assertEquals("denyPermission2", jsonElement.getAsString());
    }

}