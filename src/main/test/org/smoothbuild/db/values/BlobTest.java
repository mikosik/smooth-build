package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;

import com.google.common.hash.HashCode;

public class BlobTest {
  private final byte[] bytes = new byte[] { 1, 2, 3 };
  private final byte[] otherBytes = new byte[] { 4, 5, 6 };
  private HashedDb hashedDb;
  private TypesDb typesDb;
  private ValuesDb valuesDb;
  private BlobBuilder blobBuilder;
  private Blob blob;
  private Blob blob2;
  private HashCode hash;

  @Before
  public void before() {
    hashedDb = new TestingHashedDb();
    typesDb = new TypesDb(hashedDb);
    valuesDb = new ValuesDb(hashedDb, typesDb);
  }

  @Test
  public void creating_blob_without_content_creates_empty_blob() throws Exception {
    given(blobBuilder = valuesDb.blobBuilder());
    given(blob = blobBuilder.build());
    when(inputStreamToByteArray(blob.openInputStream()));
    thenReturned(new byte[] {});
  }

  @Test
  public void type_of_blob_is_blob() throws Exception {
    given(blob = createBlob(valuesDb, bytes));
    when(blob).type();
    thenReturned(typesDb.blob());
  }

  @Test
  public void empty_blob_is_empty() throws Exception {
    given(blobBuilder = valuesDb.blobBuilder());
    given(blobBuilder).close();
    given(blob = blobBuilder.build());
    when(inputStreamToByteArray(blob.openInputStream()));
    thenReturned(new byte[] {});
  }

  @Test
  public void blob_has_content_passed_to_builder() throws Exception {
    given(blob = createBlob(valuesDb, bytes));
    when(inputStreamToByteArray(blob.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void blob_has_content_passed_to_write_byte_method() throws Exception {
    given(blobBuilder = valuesDb.blobBuilder());
    given(blobBuilder.sink().writeByte(0x17));
    given(blob = blobBuilder.build());
    when(blob.openInputStream().read());
    thenReturned(0x17);
  }

  @Test
  public void blob_has_content_passed_to_write_byte_array_method() throws Exception {
    given(blobBuilder = valuesDb.blobBuilder());
    given(blobBuilder.sink().write(bytes));
    given(blob = blobBuilder.build());
    when(inputStreamToByteArray(blob.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void blobs_with_equal_content_are_equal() throws Exception {
    given(blob = createBlob(valuesDb, bytes));
    given(blob2 = createBlob(valuesDb, bytes));
    when(blob);
    thenReturned(blob2);
  }

  @Test
  public void blobs_with_different_content_are_not_equal() throws Exception {
    given(blob = createBlob(valuesDb, bytes));
    given(blob2 = createBlob(valuesDb, otherBytes));
    when(blob);
    thenReturned(not(blob2));
  }

  @Test
  public void hash_of_blobs_with_equal_content_is_the_same() throws Exception {
    given(blob = createBlob(valuesDb, bytes));
    given(blob2 = createBlob(valuesDb, bytes));
    when(blob.hash());
    thenReturned(blob2.hash());
  }

  @Test
  public void hash_of_blobs_with_different_content_is_not_the_same() throws Exception {
    given(blob = createBlob(valuesDb, bytes));
    given(blob2 = createBlob(valuesDb, otherBytes));
    when(blob.hash());
    thenReturned(not(blob2.hash()));
  }

  @Test
  public void hash_code_of_blob_with_equal_content_is_the_same() throws Exception {
    given(blob = createBlob(valuesDb, bytes));
    given(blob2 = createBlob(valuesDb, bytes));
    when(blob.hashCode());
    thenReturned(blob2.hashCode());
  }

  @Test
  public void hash_code_of_blobs_with_different_values_is_not_the_same() throws Exception {
    given(blob = createBlob(valuesDb, bytes));
    given(blob2 = createBlob(valuesDb, otherBytes));
    when(blob.hashCode());
    thenReturned(not(blob2.hashCode()));
  }

  @Test
  public void blob_can_be_read_by_hash() throws Exception {
    given(blob = createBlob(valuesDb, bytes));
    given(hash = blob.hash());
    when(() -> new TestingValuesDb(hashedDb).get(hash));
    thenReturned(blob);
  }

  @Test
  public void blob_read_by_hash_has_same_content() throws Exception {
    given(blob = createBlob(valuesDb, bytes));
    given(hash = blob.hash());
    when(inputStreamToByteArray(((Blob) new TestingValuesDb(hashedDb).get(hash))
        .openInputStream()));
    thenReturned(inputStreamToByteArray(blob.openInputStream()));
  }

  @Test
  public void to_string() throws Exception {
    given(blob = createBlob(valuesDb, new byte[] { 1, 2, 3 }));
    when(() -> blob.toString());
    thenReturned("Blob(...):" + blob.hash());
  }

  private static Blob createBlob(ValuesDb valuesDb, byte[] content) throws Exception {
    BlobBuilder blobBuilder = valuesDb.blobBuilder();
    blobBuilder.sink().write(content);
    return blobBuilder.build();
  }

  @Test
  public void reading_not_stored_blob_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(blob = typesDb.blob().newValue(hash));
    when(blob).openInputStream();
    thenThrown(exception(new IOException("Could not find " + hash + " object.")));
  }
}
