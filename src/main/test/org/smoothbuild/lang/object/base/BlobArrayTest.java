package org.smoothbuild.lang.object.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.testing.TestingContext;

public class BlobArrayTest extends TestingContext {
  private Array array;
  private Hash hash;

  @Test
  public void type_of_blob_array_is_blob_array() throws Exception {
    given(array = arrayBuilder(blobType()).build());
    when(array.type());
    thenReturned(arrayType(blobType()));
  }

  @Test
  public void reading_elements_from_not_stored_blob_array_fails() throws Exception {
    given(hash = Hash.of(33));
    given(array = arrayType(blobType()).newSObject(hash));
    when(array).asIterable(Blob.class);
    thenThrown(ObjectsDbException.class);
  }
}
