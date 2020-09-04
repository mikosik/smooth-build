package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class BlobArrayTest extends TestingContext {
  private Array array;

  @Test
  public void spec_of_blob_array_is_blob_array() {
    array = arrayBuilder(blobSpec()).build();
    assertThat(array.spec())
        .isEqualTo(arraySpec(blobSpec()));
  }
}
