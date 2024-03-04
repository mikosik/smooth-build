package org.smoothbuild.virtualmachine.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeClassException;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class DecodeExprWrongNodeClassExceptionTest extends TestingVirtualMachine {
  @Test
  public void message_without_index() throws Exception {
    var exception = new DecodeExprWrongNodeClassException(
        Hash.of(123), intTB(), "node-path", Integer.class, Double.class);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node has unexpected class. Expected java.lang.Integer class but was java.lang"
            + ".Double class.");
  }

  @Test
  public void message_with_index() throws Exception {
    var exception = new DecodeExprWrongNodeClassException(
        Hash.of(123), intTB(), "node-path", 7, Integer.class, Double.class);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path[7]` path in Merkle tree. "
            + "Node has unexpected class. Expected java.lang.Integer class but was java.lang"
            + ".Double class.");
  }
}
