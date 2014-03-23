package org.smoothbuild.lang.builtin.blob;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ToStringSmoothTest extends IntegrationTestCase {

  @Test
  public void test() throws IOException {
    Path path = path("source/path/file.txt");
    String content = "file content";

    fileSystem.createFile(path, content);

    script("run : file(" + path + ") | content | toString ;");
    build("run");

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(RESULTS_PATH.append(path("run")), content);
  }
}
