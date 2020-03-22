package org.smoothbuild.lang.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class BlobArrayTest extends TestingContext {
  private Array array;

  @Test
  public void type_of_blob_array_is_blob_array() {
    array = arrayBuilder(blobType()).build();
    assertThat(array.type())
        .isEqualTo(arrayType(blobType()));
  }
}
