package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContextImpl;

public class DecodeObjNodeExceptionTest extends TestingContextImpl {
  @Test
  public void message() {
    var exception = new DecodeObjNodeException(
        Hash.of(13), intSpec(), "node-path", "Detailed message.");
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Cannot decode its node at `node-path` path in Merkle tree. Detailed message.");
  }
}
