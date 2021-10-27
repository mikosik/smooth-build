package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.DecodeStructExprWrongItemsSizeException;
import org.smoothbuild.db.object.spec.expr.StructExprSpec;
import org.smoothbuild.testing.TestingContextImpl;

public class DecodeStructExprWrongItemsSizeExceptionTest extends TestingContextImpl {
  @Test
  public void message() {
    var structSpec = structSpec(
        "MyStruct", namedList(list(named("f1", intSpec()), named("f2", strSpec()))));
    StructExprSpec structExprSpec = structExprSpec(structSpec);
    var exception = new DecodeStructExprWrongItemsSizeException(Hash.of(13), structExprSpec, 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `STRUCT:MyStruct` object at "
            + "b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Evaluation spec items size (2) is not equal to actual items size (3).");
  }
}
