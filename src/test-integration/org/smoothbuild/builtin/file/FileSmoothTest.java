package org.smoothbuild.builtin.file;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class FileSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile() throws IOException {
    // given
    Path dir = path("destination/dir");
    Path path = path("file/path/file.txt");
    fileSystem.createFileContainingItsPath(path);
    script("run : file(" + path + ") | save(" + dir + ");");

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContainsItsPath(dir, path);
  }
}
