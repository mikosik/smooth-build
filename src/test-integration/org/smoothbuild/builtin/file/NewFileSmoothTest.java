package org.smoothbuild.builtin.file;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;

public class NewFileSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile_pipe() throws IOException {
    Path path = path("file/path/file.txt");
    String content = "file content";
    fileSystem.createFile(path, content);

    script("run : newFile(path=" + path + ", content='" + content + "') | save(dir='.');");

    smoothApp.run("run");

    messages.assertNoProblems();
    fileSystem.assertFileContains(path, content);
  }
}
