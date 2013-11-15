package org.smoothbuild.base;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class StringSmoothTest extends IntegrationTestCase {
  @Test
  public void escapingDoubleQuotes() throws IOException {
    // given
    script("run : '\\\"' ;");

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContains(RESULTS_PATH.append(path("run")), "\"");
  }

  @Test
  public void escapingBackslash() throws IOException {
    // given
    script("run : '\\\\' ;");

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContains(RESULTS_PATH.append(path("run")), "\\");
  }
}
