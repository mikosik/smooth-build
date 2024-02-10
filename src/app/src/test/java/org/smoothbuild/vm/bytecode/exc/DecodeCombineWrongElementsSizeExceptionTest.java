package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeCombineWrongElementsSizeException;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeCombineWrongElementsSizeExceptionTest extends TestContext {
  @Test
  public void message() throws Exception {
    var exception =
        new DecodeCombineWrongElementsSizeException(Hash.of(13), combineCB(intTB(), stringTB()), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `COMBINE:{Int,String}` object at "
            + "43c66c260828c9839f26474151db105481ff92f5e01377f75389d4ce3d2dd574. "
            + "Evaluation type elements size (2) is not equal to actual elements size (3).");
  }
}
