package org.smoothbuild.builtin.file;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.type.impl.TestFile;

public class FileSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile() throws IOException {
    // given
    Path dir = path("destination/dir");
    TestFile file = file(path("file/path/file.txt"));
    file.createContentWithFilePath();
    script("run : file(" + file.path() + ") | save(" + dir + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
    fileSet(dir).file(file.path()).assertContentContainsFilePath();
  }
}
