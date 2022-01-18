package org.smoothbuild.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeObjWrongNodeTypeExcTest extends TestingContext {
  @Test
  public void message_with_types() {
    var exception = new DecodeObjWrongNodeTypeExc(
        Hash.of(123), intTB(), "node-path", boolTB(), stringTB());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node has unexpected type. Expected `Bool` but was `String`.");
  }

  @Test
  public void message_with_index_and_types() {
    var exception = new DecodeObjWrongNodeTypeExc(
        Hash.of(123), intTB(), "node-path", 7, boolTB(), stringTB());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path[7]` path in Merkle tree. "
            + "Node has unexpected type. Expected `Bool` but was `String`.");
  }
}
