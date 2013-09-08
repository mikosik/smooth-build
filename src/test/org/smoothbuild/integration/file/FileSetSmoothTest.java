package org.smoothbuild.integration.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class FileSetSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFileSetWithTwoFiles() throws IOException {
    // given
    Path dir = path("destination/dir");
    Path file1 = path("file/path/file1.txt");
    Path file2 = path("file/path/file2.txt");

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(path='" + file1.value() + "'), file(path='" + file2.value()
        + "') ];\n");
    builder.append("run : myfiles | save(dir='" + dir.value() + "');\n");
    script(builder.toString());

    fileSystem.createFileContainingItsPath(file1);
    fileSystem.createFileContainingItsPath(file2);

    // when
    smoothRunner.run("run");

    // then
    problems.assertNoProblems();
    TestingFileSystem subFileSystem = fileSystem.subFileSystem(dir);
    subFileSystem.assertFileContainsItsPath(file1);
    subFileSystem.assertFileContainsItsPath(file2);
  }

  @Test
  public void saveFileSetWithOneFile() throws IOException {
    // given
    Path dir = path("destination/dir");
    Path file1 = path("file/path/file1.txt");

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(path='" + file1.value() + "') ];\n");
    builder.append("run : myfiles | save(dir='" + dir.value() + "');\n");
    script(builder.toString());

    fileSystem.createFileContainingItsPath(file1);

    // when
    smoothRunner.run("run");

    // then
    problems.assertNoProblems();
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(file1);
  }

  @Test
  public void saveEmptyFileSet() throws IOException {
    // given
    Path dir = path("destination/dir");

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ ];\n");
    builder.append("run : myfiles | save(dir='" + dir.value() + "');\n");
    script(builder.toString());

    // when
    smoothRunner.run("run");

    // then
    problems.assertNoProblems();
    assertThat(fileSystem.pathExists(dir)).isFalse();
  }
}
