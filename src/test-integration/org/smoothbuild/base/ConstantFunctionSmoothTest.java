package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;

public class ConstantFunctionSmoothTest extends IntegrationTestCase {
  @Test
  public void test() throws IOException {
    // given
    script("run: 'abc';");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
  }
}
