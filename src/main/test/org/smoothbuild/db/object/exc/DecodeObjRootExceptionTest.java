package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.cannotReadRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.nonNullObjRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.nullObjRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.wrongSizeOfRootSequenceException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeObjRootExceptionTest extends TestingContext {
  @Test
  public void cannot_read_root_exception() {
    var exception = cannotReadRootException(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode root.");
  }

  @Test
  public void null_obj_root_exception() {
    var exception = nullObjRootException(Hash.of(123), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Its root points to hash sequence with 3 elements when it should point to "
            + "sequence with 1 element as its spec is NULL.");
  }

  @Test
  public void non_null_obj_root_exception() {
    var exception = nonNullObjRootException(Hash.of(123), 3);
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
