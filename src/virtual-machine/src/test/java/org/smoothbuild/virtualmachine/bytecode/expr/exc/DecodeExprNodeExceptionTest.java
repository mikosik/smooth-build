package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class DecodeExprNodeExceptionTest extends VmTestContext {
  @Test
  void message() throws Exception {
    var exception =
        new DecodeExprNodeException(Hash.of(13), bIntType(), "node-path", "Detailed message.");
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` expression at "
            + "43c66c260828c9839f26474151db105481ff92f5e01377f75389d4ce3d2dd574. "
            + "Cannot decode its node at `node-path` path in Merkle tree. Detailed message.");
  }
}
