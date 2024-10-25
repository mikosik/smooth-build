package org.smoothbuild.virtualmachine.bytecode.kind.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.INT;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;

public class DecodeKindWrongChainSizeExceptionTest extends BytecodeTestContext {
  @Test
  void message() {
    var exception = new DecodeKindWrongChainSizeException(Hash.of(123), INT, "node-path", 7, 2);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode kind INT at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node is a chain with wrong size. Expected 7 but was 2.");
  }
}
