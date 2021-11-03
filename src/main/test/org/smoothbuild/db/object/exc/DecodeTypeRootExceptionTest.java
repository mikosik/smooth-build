package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.exc.DecodeTypeRootException;

public class DecodeTypeRootExceptionTest {
  @Test
  public void message() {
    var exception = new DecodeTypeRootException(Hash.of(123), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode type at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Its root points to hash sequence with 3 elements when it should point to "
            + "sequence with 1 or 2 elements.");
  }
}
