package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;

public class DecodeSpecExceptionTest {
  @Test
  public void message() {
    var exception = new DecodeSpecException(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode spec at 1959893f68220459cbd800396e1eae7bfc382e97.");
  }
}
