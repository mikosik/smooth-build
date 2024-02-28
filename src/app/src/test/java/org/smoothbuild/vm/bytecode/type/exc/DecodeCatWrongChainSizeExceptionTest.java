package org.smoothbuild.vm.bytecode.type.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.Hash;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.type.CategoryKinds;

public class DecodeCatWrongChainSizeExceptionTest extends TestContext {
  @Test
  public void message() {
    var exception =
        new DecodeCatWrongChainSizeException(Hash.of(123), CategoryKinds.INT, "node-path", 7, 2);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode INT category at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node is a chain with wrong size. Expected 7 but was 2.");
  }
}
