package org.smoothbuild.integration.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class FileSetSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFileSetWithTwoFiles() throws IOException {
    // given
    Path dir = path("destination/dir");
    Path file1 = path("file/path/file1.txt");
    Path file2 = path("file/path/file2.txt");

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + file1 + "), file(" + file2 + ") ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    fileSystem.createFileContainingItsPath(file1);
    fileSystem.createFileContainingItsPath(file2);

    // when
    smoothRunner.run("run");

    // then
    problems.assertNoProblems();
    TestFileSystem subFileSystem = fileSystem.subFileSystem(dir);
    subFileSystem.assertFileContainsItsPath(file1);
    subFileSystem.assertFileContainsItsPath(file2);
  }

  @Test
  public void saveFileSetWithOneFile() throws IOException {
    // given
    Path dir = path("destination/dir");
    Path file1 = path("file/path/file1.txt");

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + file1 + ") ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
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
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothRunner.run("run");

    // then
    problems.assertNoProblems();
    assertThat(fileSystem.pathExists(dir)).isFalse();
  }
}
