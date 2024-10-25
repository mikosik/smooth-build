package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;

public class NodeHasWrongTypeExceptionTest extends BytecodeTestContext {
  @Test
  void message_with_types() throws Exception {
    var exception = new NodeHasWrongTypeException(
        Hash.of(123), bIntType(), "node-path", bBoolType(), bStringType());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` "
            + "expression at a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node has unexpected type. Expected `Bool` but was `String`.");
  }

  @Test
  void message_with_index_and_types() throws Exception {
    var exception = new NodeHasWrongTypeException(
        Hash.of(123), bIntType(), "node-path", 7, bBoolType(), bStringType());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` expression at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path[7]` path in Merkle tree. "
            + "Node has unexpected type. Expected `Bool` but was `String`.");
  }
}
