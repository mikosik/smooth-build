package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeCombineWrongElementsSizeExc;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeCombineWrongElementsSizeExcTest extends TestContext {
  @Test
  public void message() {
    var exception = new DecodeCombineWrongElementsSizeExc(
        Hash.of(13), combineCB(intTB(), stringTB()), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `COMBINE:{Int,String}` object at "
            + "43c66c260828c9839f26474151db105481ff92f5e01377f75389d4ce3d2dd574. "
            + "Evaluation type elements size (2) is not equal to actual elements size (3).");
  }
}
