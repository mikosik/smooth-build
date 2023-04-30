package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNodeExc;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeExprNodeExcTest extends TestContext {
  @Test
  public void message() {
    var exception = new DecodeExprNodeExc(
        Hash.of(13), intTB(), "node-path", "Detailed message.");
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at "
            + "43c66c260828c9839f26474151db105481ff92f5e01377f75389d4ce3d2dd574. "
            + "Cannot decode its node at `node-path` path in Merkle tree. Detailed message.");
  }
}
