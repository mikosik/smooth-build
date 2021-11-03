package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.DecodeObjTypeException;

public class DecodeObjTypeExceptionTest {
  @Test
  public void message() {
    var exception = new DecodeObjTypeException(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its type.");
  }
}
