package org.smoothbuild.lang.object.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class BlobTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");
  private final ByteString otherBytes = ByteString.encodeUtf8("bbb");
  private BlobBuilder blobBuilder;
  private Blob blob;
  private Blob blob2;
  private Hash hash;

  @Test
  public void creating_blob_without_content_creates_empty_blob() throws Exception {
    given(blobBuilder = blobBuilder());
    given(blob = blobBuilder.build());
    when(blob.source().readByteString());
    thenReturned(ByteString.of());
  }

  @Test
  public void type_of_blob_is_blob() throws Exception {
    given(blob = blob(bytes));
    when(blob).type();
    thenReturned(blobType());
  }

  @Test
  public void empty_blob_is_empty() throws Exception {
    given(blobBuilder = blobBuilder());
    given(blob = blobBuilder.build());
    when(blob.source().readByteString());
    thenReturned(ByteString.of());
  }

  @Test
  public void blob_has_content_passed_to_builder() throws Exception {
    given(blob = blob(bytes));
    when(blob.source().readByteString());
    thenReturned(bytes);
  }

  @Test
  public void blobs_with_equal_content_are_equal() throws Exception {
    given(blob = blob(bytes));
    given(blob2 = blob(bytes));
    when(blob);
    thenReturned(blob2);
  }

  @Test
  public void blobs_with_different_content_are_not_equal() throws Exception {
    given(blob = blob(bytes));
    given(blob2 = blob(otherBytes));
    when(blob);
    thenReturned(not(blob2));
  }

  @Test
  public void hash_of_blobs_with_equal_content_is_the_same() throws Exception {
    given(blob = blob(bytes));
    given(blob2 = blob(bytes));
    when(blob.hash());
    thenReturned(blob2.hash());
  }

  @Test
  public void hash_of_blobs_with_different_content_is_not_the_same() throws Exception {
    given(blob = blob(bytes));
    given(blob2 = blob(otherBytes));
    when(blob.hash());
    thenReturned(not(blob2.hash()));
  }

  @Test
  public void hash_code_of_blob_with_equal_content_is_the_same() throws Exception {
    given(blob = blob(bytes));
    given(blob2 = blob(bytes));
    when(blob.hashCode());
    thenReturned(blob2.hashCode());
  }

  @Test
  public void hash_code_of_blobs_with_different_values_is_not_the_same() throws Exception {
    given(blob = blob(bytes));
    given(blob2 = blob(otherBytes));
    when(blob.hashCode());
    thenReturned(not(blob2.hashCode()));
  }

  @Test
  public void blob_can_be_read_by_hash() throws Exception {
    given(blob = blob(bytes));
    given(hash = blob.hash());
    when(() -> objectsDbOther().get(hash));
    thenReturned(blob);
  }

  @Test
  public void blob_read_by_hash_has_same_content() throws Exception {
    given(blob = blob(bytes));
    given(hash = blob.hash());
    when(((Blob) objectsDbOther().get(hash)).source().readByteString());
    thenReturned(blob.source().readByteString());
  }

  @Test
  public void to_string() throws Exception {
    given(blob = blob(bytes));
    when(() -> blob.toString());
    thenReturned("Blob(...):" + blob.hash());
  }

  @Test
  public void reading_not_stored_blob_fails() {
    given(hash = Hash.of(33));
    given(blob = blobType().newSObject(hash));
    when(() -> blob.source());
    thenThrown(exception(new ObjectsDbException(blob.hash(), new NoSuchDataException(hash))));
  }
}
