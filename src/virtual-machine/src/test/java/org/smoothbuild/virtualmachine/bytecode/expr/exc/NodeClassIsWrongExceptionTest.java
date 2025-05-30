package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class NodeClassIsWrongExceptionTest extends VmTestContext {
  @Test
  void message_without_index() throws Exception {
    var exception = new NodeClassIsWrongException(
        Hash.of(123), bIntType(), "node-path", Integer.class, Double.class);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` expression at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node has unexpected class. Expected java.lang.Integer class but was java.lang"
            + ".Double class.");
  }

  @Test
  void message_with_index() throws Exception {
    var exception = new NodeClassIsWrongException(
        Hash.of(123), bIntType(), "node-path", 7, Integer.class, Double.class);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` expression at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path[7]` path in Merkle tree. "
            + "Node has unexpected class. Expected java.lang.Integer class but was java.lang"
            + ".Double class.");
  }
}
