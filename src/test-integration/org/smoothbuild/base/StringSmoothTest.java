package org.smoothbuild.base;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.type.impl.TestFile;

public class StringSmoothTest extends IntegrationTestCase {
  @Test
  public void escapingDoubleQuotes() throws IOException {
    // given
    Path outDir = path("out");
    Path path = path("file.txt");
    String content = "\\\"";
    script("run : newFile(" + path + ", content='" + content + "') | save(" + outDir + ");");

    // when
    smoothApp.run("run");

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
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
    TestFile file = fileSet(outDir).file(path);
    file.assertContentContains("\\");
  }
}
