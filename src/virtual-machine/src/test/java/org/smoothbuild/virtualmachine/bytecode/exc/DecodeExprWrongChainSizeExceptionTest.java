package org.smoothbuild.virtualmachine.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongChainSizeException;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class DecodeExprWrongChainSizeExceptionTest extends TestingVirtualMachine {
  @Test
  public void message() throws Exception {
    var exception = new DecodeExprWrongChainSizeException(Hash.of(123), intTB(), "node-path", 7, 2);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node is a chain with wrong size. Expected 7 but was 2.");
  }
}
