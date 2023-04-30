package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeClassExc;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeExprWrongNodeClassExcTest extends TestContext {
  @Test
  public void message_without_index() {
    var exception = new DecodeExprWrongNodeClassExc(
        Hash.of(123), intTB(), "node-path", Integer.class, Double.class);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node has unexpected class. Expected java.lang.Integer class but was java.lang"
            + ".Double class.");
  }

  @Test
  public void message_with_index() {
    var exception = new DecodeExprWrongNodeClassExc(
        Hash.of(123), intTB(), "node-path", 7, Integer.class, Double.class);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path[7]` path in Merkle tree. "
            + "Node has unexpected class. Expected java.lang.Integer class but was java.lang"
            + ".Double class.");
  }
}
