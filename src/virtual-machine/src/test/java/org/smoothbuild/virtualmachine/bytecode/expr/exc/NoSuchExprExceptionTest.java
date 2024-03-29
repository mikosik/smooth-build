package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;

public class NoSuchExprExceptionTest {
  @Test
  public void message() {
    var exception = new NoSuchExprException(Hash.of(123));
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode expression at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot find it in expression db.");
  }
}
