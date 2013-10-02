package org.smoothbuild.builtin.file;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.plugin.internal.TestFile;

public class NewFileSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile_pipe() throws IOException {
    TestFile file = file(path("file/path/file.txt"));
    String content = "file content";

    script("run : newFile(path=" + file.path() + ", content='" + content + "') | save(dir='.');");

    smoothRunner.run("run");

    messages.assertNoProblems();
    file.assertContentContains(content);
  }
}
