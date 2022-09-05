package org.smoothbuild.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.exc.DecodeExprNodeExc;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.testing.TestContext;

public class DecodeExprNodeExcTest extends TestContext {
  @Test
  public void message() {
    var exception = new DecodeExprNodeExc(
        Hash.of(13), intTB(), "node-path", "Detailed message.");
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Cannot decode its node at `node-path` path in Merkle tree. Detailed message.");
  }
}
