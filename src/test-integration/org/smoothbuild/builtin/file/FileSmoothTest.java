package org.smoothbuild.builtin.file;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.plugin.internal.TestFile;

public class FileSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile() throws IOException {
    // given
    Path dir = path("destination/dir");
    TestFile file = file(path("file/path/file.txt"));
    file.createContentWithFilePath();
    script("run : file(" + file.path() + ") | save(" + dir + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    fileSet(dir).file(file.path()).assertContentContainsFilePath();
  }
}
