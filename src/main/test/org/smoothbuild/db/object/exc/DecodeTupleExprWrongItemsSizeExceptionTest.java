package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.DecodeTupleExprWrongItemsSizeException;
import org.smoothbuild.testing.TestingContextImpl;

public class DecodeTupleExprWrongItemsSizeExceptionTest extends TestingContextImpl {
  @Test
  public void message() {
    var exception = new DecodeTupleExprWrongItemsSizeException(
        Hash.of(13), tupleExprSpec(list(intSpec(), strSpec())), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `TUPLE:{Int,String}` object at "
            + "b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Evaluation spec items size (2) is not equal to actual items size (3).");
  }
}
