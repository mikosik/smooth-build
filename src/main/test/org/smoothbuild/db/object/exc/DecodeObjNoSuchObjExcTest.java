package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.DecodeObjNoSuchObjExc;

public class DecodeObjNoSuchObjExcTest {
  @Test
  public void message() {
    var exception = new DecodeObjNoSuchObjExc(Hash.of(123));
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot find it in object db.");
  }
}
