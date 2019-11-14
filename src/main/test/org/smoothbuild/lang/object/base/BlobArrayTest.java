package org.smoothbuild.lang.object.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.testing.TestingContext;

public class BlobArrayTest extends TestingContext {
  private Array array;

  @Test
  public void type_of_blob_array_is_blob_array() {
    given(array = arrayBuilder(blobType()).build());
    when(array.type());
    thenReturned(arrayType(blobType()));
  }
}
