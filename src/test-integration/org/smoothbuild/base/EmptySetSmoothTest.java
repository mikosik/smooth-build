package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class EmptySetSmoothTest extends IntegrationTestCase {
  @Test
  public void escapingDoubleQuotes() throws IOException {
    // given
    script("run : [];");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
  }
}
