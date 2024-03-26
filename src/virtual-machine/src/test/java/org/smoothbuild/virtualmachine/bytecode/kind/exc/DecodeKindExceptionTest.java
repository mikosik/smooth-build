package org.smoothbuild.virtualmachine.bytecode.kind.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;

public class DecodeKindExceptionTest {
  @Test
  public void message() {
    var exception = new DecodeKindException(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode kind at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea.");
  }
}
