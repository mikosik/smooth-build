package org.smoothbuild.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.exc.DecodeExprCatExc;
import org.smoothbuild.db.Hash;

public class DecodeExprCatExcTest {
  @Test
  public void message() {
    var exception = new DecodeExprCatExc(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its category.");
  }
}
