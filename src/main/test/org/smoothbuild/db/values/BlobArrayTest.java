package org.smoothbuild.db.values;

import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.arrayOf;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;

import com.google.common.hash.HashCode;

public class BlobArrayTest {
  private ValuesDb valuesDb;
  private Array array;
  private HashCode hash;

  @Before
  public void before() {
    valuesDb = memoryValuesDb();
  }

  @Test
  public void type_of_blob_array_is_blob_array() throws Exception {
    given(array = valuesDb.arrayBuilder(BLOB).build());
    when(array.type());
    thenReturned(arrayOf(BLOB));
  }

  @Test
  public void reading_elements_from_not_stored_blob_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = valuesDb.read(arrayOf(BLOB), hash));
    when(array).asIterable(Blob.class);
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }
}
