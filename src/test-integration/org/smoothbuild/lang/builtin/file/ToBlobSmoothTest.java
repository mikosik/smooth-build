package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ToBlobSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile_pipe() throws IOException {
    Path path = path("file/path/file.txt");
    String content = "file content";
    fileSystem.createFile(path, content);

    script("run : file(" + path + ") | toBlob ;");

    build("run");

    userConsole.messageGroup().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContains(artifactPath, content);
  }
}
