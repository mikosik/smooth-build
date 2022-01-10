package org.smoothbuild.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.exc.DecodeCombineWrongItemsSizeExc;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeCombineWrongItemsSizeExcTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeCombineWrongItemsSizeExc(
        Hash.of(13), combineCB(list(intTB(), stringTB())), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Combine:{Int,String}` object at "
            + "b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Evaluation type items size (2) is not equal to actual items size (3).");
  }
}