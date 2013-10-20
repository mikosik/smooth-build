package org.smoothbuild.builtin.file;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.type.impl.FakeFile;

public class NewFileSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile_pipe() throws IOException {
    FakeFile file = file(path("file/path/file.txt"));
    String content = "file content";

    script("run : newFile(path=" + file.path() + ", content='" + content + "') | save(dir='.');");

    smoothApp.run("run");

    messages.assertNoProblems();
    file.assertContentContains(content);
  }
}
