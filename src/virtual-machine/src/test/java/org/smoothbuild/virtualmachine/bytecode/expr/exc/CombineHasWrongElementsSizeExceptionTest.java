package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.TestingVm;

public class CombineHasWrongElementsSizeExceptionTest extends TestingVm {
  @Test
  void message() throws Exception {
    var exception = new CombineHasWrongElementsSizeException(
        Hash.of(13), bCombineKind(bIntType(), bStringType()), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `COMBINE` expression at "
            + "43c66c260828c9839f26474151db105481ff92f5e01377f75389d4ce3d2dd574. "
            + "Evaluation type elements size (2) is not equal to actual elements size (3).");
  }
}
