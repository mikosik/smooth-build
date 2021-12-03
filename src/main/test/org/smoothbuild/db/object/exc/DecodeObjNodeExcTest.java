package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.DecodeObjNodeExc;
import org.smoothbuild.testing.TestingContext;

public class DecodeObjNodeExcTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeObjNodeExc(
        Hash.of(13), intTH(), "node-path", "Detailed message.");
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Cannot decode its node at `node-path` path in Merkle tree. Detailed message.");
  }
}
