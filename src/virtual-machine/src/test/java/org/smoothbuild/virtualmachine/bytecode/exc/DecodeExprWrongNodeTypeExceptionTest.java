package org.smoothbuild.virtualmachine.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class DecodeExprWrongNodeTypeExceptionTest extends TestVirtualMachine {
  @Test
  public void message_with_types() throws Exception {
    var exception = new DecodeExprWrongNodeTypeException(
        Hash.of(123), intTB(), "node-path", boolTB(), stringTB());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` "
            + "object at a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node has unexpected type. Expected `Bool` but was `String`.");
  }

  @Test
  public void message_with_index_and_types() throws Exception {
    var exception = new DecodeExprWrongNodeTypeException(
        Hash.of(123), intTB(), "node-path", 7, boolTB(), stringTB());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path[7]` path in Merkle tree. "
            + "Node has unexpected type. Expected `Bool` but was `String`.");
  }
}