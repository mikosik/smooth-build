package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeSelectIndexOutOfBoundsExceptionTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeSelectIndexOutOfBoundsException(
        Hash.of(13), selectSpec(intSpec()), 13, 10);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode SELECT:INT object "
            + "at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Its index component is 13 while RECORD size is 10.");
  }
}
