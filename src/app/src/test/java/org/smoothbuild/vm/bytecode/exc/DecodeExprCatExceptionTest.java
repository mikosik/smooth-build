package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.Hash;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprCatException;

public class DecodeExprCatExceptionTest {
  @Test
  public void message() {
    var exception = new DecodeExprCatException(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode its category.");
  }
}
