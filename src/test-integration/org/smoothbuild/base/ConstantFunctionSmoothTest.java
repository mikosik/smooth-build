package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ConstantFunctionSmoothTest extends IntegrationTestCase {
  @Test
  public void test() throws IOException {
    // given
    script("run: 'abc';");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
  }
}
