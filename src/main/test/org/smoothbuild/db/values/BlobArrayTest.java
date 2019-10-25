package org.smoothbuild.db.values;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.testing.TestingContext;

import com.google.common.hash.HashCode;

public class BlobArrayTest extends TestingContext {
  private Array array;
  private HashCode hash;

  @Test
  public void type_of_blob_array_is_blob_array() throws Exception {
    given(array = arrayBuilder(blobType()).build());
    when(array.type());
    thenReturned(arrayType(blobType()));
  }

  @Test
  public void reading_elements_from_not_stored_blob_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = arrayType(blobType()).newValue(hash));
    when(array).asIterable(Blob.class);
    thenThrown(ValuesDbException.class);
  }
}
