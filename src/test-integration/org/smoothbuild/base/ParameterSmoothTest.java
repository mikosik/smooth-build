package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ParameterSmoothTest extends IntegrationTestCase {
  @Test
  public void trailingCommaIsAllowedInParameterList() throws IOException {
    // given
    script("run : newFile(path='file.txt', content='abc',) | save('outptuDir',);");

    // when
    smoothApp.run("run");

    // then
    userConsole.assertNoProblems();
  }
}
