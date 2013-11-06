package org.smoothbuild.base;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class StringSmoothTest extends IntegrationTestCase {
  @Test
  public void escapingDoubleQuotes() throws IOException {
    // given
    Path path = path("file.txt");
    String content = "\\\"";
    script("run : newFile(" + path + ", content='" + content + "') | save('.');");

    // when
    smoothApp.run("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContains(path, "\"");
  }

  @Test
  public void escapingBackslash() throws IOException {
    // given
    Path path = path("file.txt");
    String content = "\\\\";
    script("run : newFile(" + path + ", content='" + content + "') | save('.');");

    // when
    smoothApp.run("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContains(path, "\\");
  }
}
