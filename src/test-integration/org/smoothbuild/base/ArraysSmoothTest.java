package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ArraysSmoothTest extends IntegrationTestCase {
  @Test
  public void nested_arrays_are_forbidden() throws IOException {
    // given
    script("run : [ [ 'abc' ] ];");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(SyntaxError.class);
  }
}
