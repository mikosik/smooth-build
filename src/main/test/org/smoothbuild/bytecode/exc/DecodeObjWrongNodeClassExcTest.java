package org.smoothbuild.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeClassExc;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeObjWrongNodeClassExcTest extends TestingContext {
  @Test
  public void message_without_index() {
    var exception = new DecodeObjWrongNodeClassExc(
        Hash.of(123), intTB(), "node-path", Integer.class, Double.class);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node has unexpected class. Expected java.lang.Integer class but was java.lang"
            + ".Double class.");
  }

  @Test
  public void message_with_index() {
    var exception = new DecodeObjWrongNodeClassExc(
        Hash.of(123), intTB(), "node-path", 7, Integer.class, Double.class);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path[7]` path in Merkle tree. "
            + "Node has unexpected class. Expected java.lang.Integer class but was java.lang"
            + ".Double class.");
  }
}
