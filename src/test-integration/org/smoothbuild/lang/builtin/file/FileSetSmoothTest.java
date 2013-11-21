package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class FileSetSmoothTest extends IntegrationTestCase {
  Path path1 = path("file/path/file1.txt");
  Path path2 = path("file/path/file2.txt");

  @Test
  public void setWithTrailingCommaIsAllowed() throws Exception {
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    StringBuilder builder = new StringBuilder();
    builder.append("run : [ file(" + path1 + "), file(" + path2 + "), ];\n");
    script(builder.toString());

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

  @Test
  public void setWithOnlyCommaIsForbidden() throws Exception {
    // given
    script("run : [ , ];");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(SyntaxError.class);
  }

  @Test
  public void saveFileSetWithTwoFiles() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    StringBuilder builder = new StringBuilder();
    builder.append("run : [ file(" + path1 + "), file(" + path2 + ") ];\n");
    script(builder.toString());

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

  @Test
  public void saveFileSetWithOneFile() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path1);

    StringBuilder builder = new StringBuilder();
    builder.append("run : [ file(" + path1 + ") ];\n");
    script(builder.toString());

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
  }

  @Test
  public void saveEmptyFileSet() throws IOException {
    // given
    StringBuilder builder = new StringBuilder();
    builder.append("run : [ ];\n");
    script(builder.toString());

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
  }

  @Test
  public void file_set_can_contain_duplicate_values() throws Exception {
    // given
    fileSystem.createFileContainingItsPath(path1);

    script("run : [ file(" + path1 + "), file(" + path1 + ") ] | filter('nothing');\n");

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
  }
}
