package com.liferay.workplace.search.client;

import com.liferay.workplace.search.client.annotation.AllowPermissions;
import com.liferay.workplace.search.client.annotation.DenyPermissions;
import com.liferay.workplace.search.client.annotation.Field;
import com.liferay.workplace.search.client.response.Document;
import com.liferay.workplace.search.client.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import static org.junit.Assert.*;

public class WorkplaceSearchClientIntegrationTest {

    @Before
    public void setUp() throws IOException {
        Properties props = new Properties();
        InputStream is = ClassLoader.getSystemResourceAsStream("test.properties");
        props.load(is);

        client = new WorkplaceSearchClient.Builder(
                props.getProperty("host"), props.getProperty("key"), props.getProperty("token"))
            .build();
    }

    @Test
    public void testIndexDocuments() throws Exception {
        Document document = indexDocument();;
        assertFalse(document.id().isEmpty());
    }

    @Test
    public void testDestroyDocuments() throws Exception {
        Document indexedDocument = indexDocument();

        Response<Document> response = client.destroyDocuments(Collections.singletonList(indexedDocument.id())).execute();

        Collection<Document> documents = response.body();
        assertEquals(1, documents.size());

        Document deletedDocument = documents.stream().findFirst().get();

        assertEquals(indexedDocument.id(), deletedDocument.id());
        assertTrue(deletedDocument.isSuccess());
    }

    protected Document indexDocument() throws Exception {
        Blog blog = new Blog("Bruno Farache", "Document Title");

        Response<Document> response = client.indexDocuments(Collections.singletonList(blog)).execute();

        assertTrue(response.isSuccessful());

        Collection<Document> documents = response.body();
        assertEquals(1, documents.size());

        return documents.stream().findFirst().get();
    }

    class Blog {

        public Blog(String author, String title) {
            this.author = author;
            _title = title;
        }

        @AllowPermissions
        public String[] getAllowPermissions() {
            return new String[]{"allowPermission1", "allowPermission2"};
        }

        public String getAuthor() {
            return author;
        }

        @DenyPermissions
        public String[] getDenyPermissions() {
            return new String[]{"denyPermission1", "denyPermission2"};
        }

        public String getTitle() {
            return _title;
        }

        @Field
        private String author;

        @Field(name = "title")
        private String _title;

        private String _secret = "this is a secret";
    }

    protected WorkplaceSearchClient client;

}