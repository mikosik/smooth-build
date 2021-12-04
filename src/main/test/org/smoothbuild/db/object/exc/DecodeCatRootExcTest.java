package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.exc.DecodeCatRootExc;

public class DecodeCatRootExcTest {
  @Test
  public void message() {
    var exception = new DecodeCatRootExc(Hash.of(123), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode category at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Its root points to hash sequence with 3 elems when it should point to "
            + "sequence with 1 or 2 elems.");
  }
}
