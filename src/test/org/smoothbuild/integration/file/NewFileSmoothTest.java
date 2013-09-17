package org.smoothbuild.integration.file;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;

public class NewFileSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile_pipe() throws IOException {
    Path file = path("file/path/file.txt");
    String content = "file content";

    script("run : newFile(path='" + file.value() + "', content='" + content + "') | save(dir='.');");

    smoothRunner.run("run");

    messages.assertNoProblems();
    fileSystem.assertFileContains(file, content);
  }
}
