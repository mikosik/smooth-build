package org.smoothbuild.db.objects;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SBlob;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class SBlobTest {
  private final String string = "abc";
  private final String otherString = "def";
  private ObjectsDb objectsDb;
  private BlobBuilder blobBuilder;
  private SBlob blob;
  private SBlob blob2;
  private HashCode hash;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestObjectsDbModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
  }

  @Test
  public void creating_blob_without_content_fails() throws Exception {
    given(blobBuilder = objectsDb.blobBuilder());
    when(blobBuilder).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void opening_output_stream_twice_is_forbidden() throws Exception {
    given(blobBuilder = objectsDb.blobBuilder());
    when(blobBuilder.openOutputStream()).close();
    when(blobBuilder).openOutputStream();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void type_of_blob_is_blob() throws Exception {
    given(blob = createBlob(objectsDb, string));
    when(blob).type();
    thenReturned(BLOB);
  }

  @Test
  public void empty_blob_is_empty() throws Exception {
    given(blobBuilder = objectsDb.blobBuilder());
    given(blobBuilder.openOutputStream()).close();
    given(blob = blobBuilder.build());
    when(inputStreamToString(blob.openInputStream()));
    thenReturned("");
  }

  @Test
  public void blob_has_content_passed_to_builder() throws Exception {
    given(blob = createBlob(objectsDb, string));
    when(inputStreamToString(blob.openInputStream()));
    thenReturned(string);
  }

  @Test
  public void blobs_with_equal_content_are_equal() throws Exception {
    given(blob = createBlob(objectsDb, string));
    given(blob2 = createBlob(objectsDb, string));
    when(blob);
    thenReturned(blob2);
  }

  @Test
  public void blobs_with_different_content_are_not_equal() throws Exception {
    given(blob = createBlob(objectsDb, string));
    given(blob2 = createBlob(objectsDb, otherString));
    when(blob);
    thenReturned(not(blob2));
  }

  @Test
  public void hash_of_blobs_with_equal_content_is_the_same() throws Exception {
    given(blob = createBlob(objectsDb, string));
    given(blob2 = createBlob(objectsDb, string));
    when(blob.hash());
    thenReturned(blob2.hash());
  }

  @Test
  public void hash_of_blobs_with_different_content_is_not_the_same() throws Exception {
    given(blob = createBlob(objectsDb, string));
    given(blob2 = createBlob(objectsDb, otherString));
    when(blob.hash());
    thenReturned(not(blob2.hash()));
  }

  @Test
  public void hash_code_of_blob_with_equal_content_is_the_same() throws Exception {
    given(blob = createBlob(objectsDb, string));
    given(blob2 = createBlob(objectsDb, string));
    when(blob.hashCode());
    thenReturned(blob2.hashCode());
  }

  @Test
  public void hash_code_of_blobs_with_different_values_is_not_the_same() throws Exception {
    given(blob = createBlob(objectsDb, string));
    given(blob2 = createBlob(objectsDb, otherString));
    when(blob.hashCode());
    thenReturned(not(blob2.hashCode()));
  }

  @Test
  public void blob_can_be_fetch_by_hash() throws Exception {
    given(blob = createBlob(objectsDb, string));
    given(hash = blob.hash());
    when(objectsDb.read(BLOB, hash));
    thenReturned(blob);
  }

  @Test
  public void blob_fetched_by_hash_has_same_content() throws Exception {
    given(blob = createBlob(objectsDb, string));
    given(hash = blob.hash());
    when(inputStreamToString(objectsDb.read(BLOB, hash).openInputStream()));
    thenReturned(inputStreamToString(blob.openInputStream()));
  }

  @Test
  public void to_string_contains_type_name_and_bytes_count() throws Exception {
    given(blob = createBlob(objectsDb, "abc"));
    when(blob).toString();
    thenReturned("Blob(3 bytes)");
  }

  private static SBlob createBlob(ObjectsDb objectsDb, String content) throws Exception {
    BlobBuilder blobBuilder = objectsDb.blobBuilder();
    writeAndClose(blobBuilder.openOutputStream(), content);
    return blobBuilder.build();
  }
}
