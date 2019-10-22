package org.smoothbuild.db.values;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;

import com.google.common.hash.HashCode;

public class BlobArrayTest {
  private ValuesDb valuesDb;
  private Array array;
  private HashCode hash;

  @Before
  public void before() {
    HashedDb hashedDb = new TestingHashedDb();
    valuesDb = new ValuesDb(hashedDb);
  }

  @Test
  public void type_of_blob_array_is_blob_array() throws Exception {
    given(array = valuesDb.arrayBuilder(valuesDb.blobType()).build());
    when(array.type());
    thenReturned(valuesDb.arrayType(valuesDb.blobType()));
  }

  @Test
  public void reading_elements_from_not_stored_blob_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = valuesDb.arrayType(valuesDb.blobType()).newValue(hash));
    when(array).asIterable(Blob.class);
    thenThrown(ValuesDbException.class);
  }
}
