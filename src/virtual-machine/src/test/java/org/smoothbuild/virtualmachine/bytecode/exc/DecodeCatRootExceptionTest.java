package org.smoothbuild.virtualmachine.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatRootException;

public class DecodeCatRootExceptionTest {
  @Test
  public void message() {
    var exception = new DecodeCatRootException(Hash.of(123), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode category at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Its root points to hash sequence with 3 elems when it should point to "
            + "sequence with 1 or 2 elems.");
  }
}
