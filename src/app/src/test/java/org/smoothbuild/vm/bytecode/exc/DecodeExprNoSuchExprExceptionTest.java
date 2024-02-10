package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNoSuchExprException;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeExprNoSuchExprExceptionTest {
  @Test
  public void message() {
    var exception = new DecodeExprNoSuchExprException(Hash.of(123));
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot find it in bytecode db.");
  }
}
