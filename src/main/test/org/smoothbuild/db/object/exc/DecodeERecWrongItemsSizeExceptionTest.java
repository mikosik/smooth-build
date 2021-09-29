package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeERecWrongItemsSizeExceptionTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeERecWrongItemsSizeException(
        Hash.of(13), eRecSpec(list(intSpec(), strSpec())), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode ERECORD:{INT,STRING} object at "
            + "b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Evaluation spec items size (2) is not equal to actual items size (3).");
  }
}
