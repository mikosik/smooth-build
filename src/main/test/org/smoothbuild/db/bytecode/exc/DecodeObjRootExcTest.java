package org.smoothbuild.db.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.bytecode.obj.exc.DecodeObjRootExc.cannotReadRootException;
import static org.smoothbuild.db.bytecode.obj.exc.DecodeObjRootExc.wrongSizeOfRootSeqException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeObjRootExcTest extends TestingContext {
  @Test
  public void cannot_read_root_exception() {
    var exception = cannotReadRootException(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode root.");
  }

  @Test
  public void wrong_size_of_root_exception() {
    var exception = wrongSizeOfRootSeqException(Hash.of(123), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Its root points to hash sequence with 3 elems when it should point to "
            + "sequence with 2 elems.");
  }
}
