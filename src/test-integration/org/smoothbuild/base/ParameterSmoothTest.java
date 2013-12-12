package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ParameterSmoothTest extends IntegrationTestCase {
  @Test
  public void trailingCommaIsAllowedInParameterList() throws IOException {
    // given
    script("run : toBlob(string='abc',) ;");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
  }
}
