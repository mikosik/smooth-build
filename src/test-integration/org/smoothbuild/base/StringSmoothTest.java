package org.smoothbuild.base;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.plugin.internal.TestFile;

public class StringSmoothTest extends IntegrationTestCase {
  @Test
  public void escapingDoubleQuotes() throws IOException {
    // given
    Path outDir = path("out");
    Path path = path("file.txt");
    String content = "\\\"";
    script("run : newFile(" + path + ", content='" + content + "') | save(" + outDir + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    TestFile file = fileSet(outDir).file(path);
    file.assertContentContains("\"");
  }

  @Test
  public void escapingBackslash() throws IOException {
    // given
    Path outDir = path("out");
    Path path = path("file.txt");
    String content = "\\\\";
    script("run : newFile(" + path + ", content='" + content + "') | save(" + outDir + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    TestFile file = fileSet(outDir).file(path);
    file.assertContentContains("\\");
  }
}
