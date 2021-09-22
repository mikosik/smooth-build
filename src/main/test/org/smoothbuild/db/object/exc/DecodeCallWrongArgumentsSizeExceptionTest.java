package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeCallWrongArgumentsSizeExceptionTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeCallWrongArgumentsSizeException(
        Hash.of(13), callSpec(intSpec()), 7, 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode CALL:INT object at b1197c208248d0f7ffb3e322d5ec187441dc1b26."
            + " Function evaluation spec parameters size (7) is not equal to arguments size (3).");
  }
}
