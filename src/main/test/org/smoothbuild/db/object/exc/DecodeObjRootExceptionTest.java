package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.cannotReadRootException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.objRootException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.wrongSizeOfRootSequenceException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContextImpl;

public class DecodeObjRootExceptionTest extends TestingContextImpl {
  @Test
  public void cannot_read_root_exception() {
    var exception = cannotReadRootException(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode root.");
  }

  @Test
  public void obj_root_exception() {
    var exception = objRootException(Hash.of(123), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Its root points to hash sequence with 3 elements when it should point to "
            + "sequence with 2 elements as its spec is not NULL.");
  }

  @Test
  public void wrong_size_of_root_exception() {
    var exception = wrongSizeOfRootSequenceException(Hash.of(123), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Its root points to hash sequence with 3 elements when it should point to "
            + "sequence with 1 or 2 elements.");
  }
}
