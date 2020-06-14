## Usage

Create a new instance of the client with your instance endpoint, key and access token:

```java
WorkplaceSearchClient client = new WorkplaceSearchClient.Builder(
      "http://localhost:3002", "5ec897b577b5b", "fb411ea6c0458099e99e")
  .build();
```

### Indexing Documents

Call the `indexDocuments` passing a json array of the documents you want to index:

```java
Response<Document> response = client.indexDocuments("[{'title': 'Blog Title', 'author': 'John Doe'}]").execute();

Collection<Document> documents = response.body();
Document document = documents.stream().findFirst().get();
String id = document.id();
```

This is a synchronous request, if you want to call asynchronously, call the `async` method instead of `execute()`, passing the success and failure callbacks:

```java
client.indexDocuments("[{'title': 'Blog Title', 'author': 'John Doe'}]").
   async(
      response -> {
            if (response.isSuccessful()) {
                  Collection<Document> documents = response.body();
                  Document document = documents.stream().findFirst().get();
                  String id = document.id();
            }
      },
      exception -> {
          // catch exception
      }
   );
```

### Destroying Documents

Pass a list of document ids to the `destroyDocuments` method to destroy them:

```java
Response<Document> response = client.destroyDocuments(Arrays.asList("1", "2")).execute();

Collection<Document> documents = response.body();
Document document = documents.stream().findFirst().get();
String id = document.id();
```

The same way as before, call `async()` instead to make it asynchronous.

### Indexing Documents Objects with Annotations

Annotate your document with the `@Field`, `@AllowPermissions` and `@DenyPermissions` annotations:

```java
public class Blog {

    @Field(name = "user")
    private String author;

    private String title;

    public Blog(String author, String title) {
        this.author = author;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
    
    @Field(name = "title")
    public String getTitle() {
        return title;
    }

    @AllowPermissions
    public String[] getAllowPermissions() {
        return new String[]{"allowPermission1", "allowPermission2"};
    }

    @DenyPermissions
    public String[] getDenyPermissions() {
        return new String[]{"denyPermission"};
    }
}
```

You can use all annotations in both getters and class fields. Annotations in methods takes precedence over annotations in fields.

The `@Field` annotation accepts an optional `name` property in case you want to change your field name to some other json attribute name (e.g., in the example above, `author` is translated to `user` in the json, if `name` was omitted, the attribute would be called `author`).

Then, call the `indexDocuments` method and then `execute()`:

```java
Blog blog = new Blog("John Doe", "Blog Title");

Response<Document> response = client.indexDocuments(Arrays.asList(blog)).execute();

if (response.isSuccessful()) {
      Collection<Document> documents = response.body();
      Document document = documents.stream().findFirst().get();
      String id = document.id();
}
```

A request will be made to the `bulk_create` endpoint, with the following json: 

```json
{
      "_allow_permissions": ["allowPermission1", "allowPermission2"],
      "_deny_permissions": ["denyPermission"],
      "user" : "John Doe",
      "title": "Blog Title"
}
```

### Customizing Document Parser

It's also possible to write your own document parser in case you don't want to use the annotations bellow:

```java
WorkplaceSearchClient client = new WorkplaceSearchClient.Builder(
      "http://localhost:3002", "5ec897b577b5b", "fb411ea6c0458099e99e")
  .useParser(customParser)
  .build();
```

Your customParser must implement the following interface:

```java
public interface Parser {

	JsonObject toJsonObject(Object object) throws Exception;

}
```
