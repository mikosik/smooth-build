package org.smoothbuild.db.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.bytecode.type.exc.DecodeCatExc;
import org.smoothbuild.db.hashed.Hash;

public class DecodeCatExcTest {
  @Test
  public void message() {
    var exception = new DecodeCatExc(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode category at 1959893f68220459cbd800396e1eae7bfc382e97.");
  }
}
