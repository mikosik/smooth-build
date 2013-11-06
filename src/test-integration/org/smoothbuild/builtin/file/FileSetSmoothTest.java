package org.smoothbuild.builtin.file;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.task.base.err.DuplicatePathError;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class FileSetSmoothTest extends IntegrationTestCase {
  Path path1 = path("file/path/file1.txt");
  Path path2 = path("file/path/file2.txt");

  @Test
  public void setWithTrailingCommaIsAllowed() throws Exception {
    Path dir = path("destination/dir");
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + path1 + "), file(" + path2 + "), ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothApp.run("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContainsItsPath(dir, path1);
    fileSystem.assertFileContainsItsPath(dir, path2);
  }

  @Test
  public void setWithOnlyCommaIsForbidden() throws Exception {
    // given
    script("run : [ , ];");

    // when
    smoothApp.run("run");

    // then
    userConsole.assertOnlyProblem(SyntaxError.class);
  }

  @Test
  public void saveFileSetWithTwoFiles() throws IOException {
    // given
    Path dir = path("destination/dir");
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + path1 + "), file(" + path2 + ") ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothApp.run("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContainsItsPath(dir, path1);
    fileSystem.assertFileContainsItsPath(dir, path2);
  }

  @Test
  public void saveFileSetWithOneFile() throws IOException {
    // given
    Path dir = path("destination/dir");

    fileSystem.createFileContainingItsPath(path1);

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + path1 + ") ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothApp.run("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContainsItsPath(dir, path1);
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
    smoothApp.run("run");

    // then
    userConsole.assertNoProblems();
  }

  @Test
  public void fileSetWithDuplicatedFiles() throws Exception {
    // given
    fileSystem.createFileContainingItsPath(path1);

    script("run : [ file(" + path1 + "), file(" + path1 + ") ];\n");

    // when
    smoothApp.run("run");

    // then
    userConsole.assertOnlyProblem(DuplicatePathError.class);
  }
}
