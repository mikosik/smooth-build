package org.smoothbuild.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class FileSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile() throws IOException {
    // given
    Path path = path("file/path/file.txt");
    String content = "file content";
    fileSystem.createFile(path, content);
    script("run : file(" + path + ") ;");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContains(artifactPath, content);
  }
}
