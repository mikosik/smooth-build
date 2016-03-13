package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.smoothbuild.util.Streams.writeAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;

import com.google.common.hash.HashCode;

public class BlobTest {
  private final String string = "abc";
  private final String otherString = "def";
  private ValuesDb valuesDb;
  private BlobBuilder blobBuilder;
  private Blob blob;
  private Blob blob2;
  private HashCode hash;

  @Before
  public void before() {
    valuesDb = memoryValuesDb();
  }

  @Test
  public void creating_blob_without_content_creates_empty_blob() throws Exception {
    given(blobBuilder = valuesDb.blobBuilder());
    given(blob = blobBuilder.build());
    when(inputStreamToString(blob.openInputStream()));
    thenReturned("");
  }

  @Test
  public void type_of_blob_is_blob() throws Exception {
    given(blob = createBlob(valuesDb, string));
    when(blob).type();
    thenReturned(BLOB);
  }

  @Test
  public void empty_blob_is_empty() throws Exception {
    given(blobBuilder = valuesDb.blobBuilder());
    given(blobBuilder).close();
    given(blob = blobBuilder.build());
    when(inputStreamToString(blob.openInputStream()));
    thenReturned("");
  }

  @Test
  public void blob_has_content_passed_to_builder() throws Exception {
    given(blob = createBlob(valuesDb, string));
    when(inputStreamToString(blob.openInputStream()));
    thenReturned(string);
  }

  @Test
  public void blob_has_content_passed_to_write_byte_method() throws Exception {
    given(blobBuilder = valuesDb.blobBuilder());
    given(blobBuilder).write(0x17);
    given(blob = blobBuilder.build());
    when(blob.openInputStream().read());
    thenReturned(0x17);
  }

  @Test
  public void blob_has_content_passed_to_write_byte_array_method() throws Exception {
    given(blobBuilder = valuesDb.blobBuilder());
    given(blobBuilder).write("abc".getBytes());
    given(blob = blobBuilder.build());
    when(inputStreamToString(blob.openInputStream()));
    thenReturned("abc");
  }

  @Test
  public void blob_has_content_passed_to_write_byte_array_with_range_method() throws Exception {
    given(blobBuilder = valuesDb.blobBuilder());
    given(blobBuilder).write("-abc-".getBytes(), 1, 3);
    given(blob = blobBuilder.build());
    when(inputStreamToString(blob.openInputStream()));
    thenReturned("abc");
  }

  @Test
  public void blobs_with_equal_content_are_equal() throws Exception {
    given(blob = createBlob(valuesDb, string));
    given(blob2 = createBlob(valuesDb, string));
    when(blob);
    thenReturned(blob2);
  }

  @Test
  public void blobs_with_different_content_are_not_equal() throws Exception {
    given(blob = createBlob(valuesDb, string));
    given(blob2 = createBlob(valuesDb, otherString));
    when(blob);
    thenReturned(not(blob2));
  }

  @Test
  public void hash_of_blobs_with_equal_content_is_the_same() throws Exception {
    given(blob = createBlob(valuesDb, string));
    given(blob2 = createBlob(valuesDb, string));
    when(blob.hash());
    thenReturned(blob2.hash());
  }

  @Test
  public void hash_of_blobs_with_different_content_is_not_the_same() throws Exception {
    given(blob = createBlob(valuesDb, string));
    given(blob2 = createBlob(valuesDb, otherString));
    when(blob.hash());
    thenReturned(not(blob2.hash()));
  }

  @Test
  public void hash_code_of_blob_with_equal_content_is_the_same() throws Exception {
    given(blob = createBlob(valuesDb, string));
    given(blob2 = createBlob(valuesDb, string));
    when(blob.hashCode());
    thenReturned(blob2.hashCode());
  }

  @Test
  public void hash_code_of_blobs_with_different_values_is_not_the_same() throws Exception {
    given(blob = createBlob(valuesDb, string));
    given(blob2 = createBlob(valuesDb, otherString));
    when(blob.hashCode());
    thenReturned(not(blob2.hashCode()));
  }

  @Test
  public void blob_can_be_fetch_by_hash() throws Exception {
    given(blob = createBlob(valuesDb, string));
    given(hash = blob.hash());
    when(valuesDb.read(BLOB, hash));
    thenReturned(blob);
  }

  @Test
  public void blob_fetched_by_hash_has_same_content() throws Exception {
    given(blob = createBlob(valuesDb, string));
    given(hash = blob.hash());
    when(inputStreamToString(((Blob) valuesDb.read(BLOB, hash)).openInputStream()));
    thenReturned(inputStreamToString(blob.openInputStream()));
  }

  @Test
  public void to_string_contains_type_name_and_bytes_count() throws Exception {
    given(blob = createBlob(valuesDb, "abc"));
    when(blob).toString();
    thenReturned("Blob(3 bytes)");
  }

  private static Blob createBlob(ValuesDb valuesDb, String content) throws Exception {
    BlobBuilder blobBuilder = valuesDb.blobBuilder();
    writeAndClose(blobBuilder, content);
    return blobBuilder.build();
  }

  @Test
  public void reading_not_stored_blob_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(blob = (Blob) valuesDb.read(BLOB, hash));
    when(blob).openInputStream();
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }
}
