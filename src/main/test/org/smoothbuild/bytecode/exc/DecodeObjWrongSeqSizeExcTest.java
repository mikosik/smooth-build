package org.smoothbuild.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongSeqSizeExc;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestContext;

public class DecodeObjWrongSeqSizeExcTest extends TestContext {
  @Test
  public void message() {
    var exception = new DecodeObjWrongSeqSizeExc(Hash.of(123), intTB(), "node-path", 7, 2);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node is a sequence with wrong size. Expected 7 but was 2.");
  }
}
