package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class TupleGetHasIndexOutOfBoundExceptionTest extends VmTestContext {
  @Test
  void message() throws Exception {
    var exception =
        new TupleGetHasIndexOutOfBoundException(Hash.of(13), bTupleGetKind(bIntType()), 13, 10);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `TUPLE_GET` expression "
            + "at 43c66c260828c9839f26474151db105481ff92f5e01377f75389d4ce3d2dd574. "
            + "Its index component is 13 while TUPLE size is 10.");
  }
}
