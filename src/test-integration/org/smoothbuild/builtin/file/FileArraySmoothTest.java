package org.smoothbuild.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class FileArraySmoothTest extends IntegrationTestCase {
  Path path1 = path("file/path/file1.txt");
  Path path2 = path("file/path/file2.txt");

  @Test
  public void arrayWithTrailingCommaIsAllowed() throws Exception {
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    script("run : [ file(" + path1 + "), file(" + path2 + "), ];");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

  @Test
  public void arrayWithOnlyCommaIsForbidden() throws Exception {
    // given
    script("run : [ , ];");

    // when
    build("run");

    // then
    userConsole.messages().assertContainsOnly(SyntaxError.class);
  }

  @Test
  public void saveFileArrayWithTwoFiles() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    script("run : [ file(" + path1 + "), file(" + path2 + ") ];");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

  @Test
  public void saveFileArrayWithOneFile() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path1);

    script("run : [ file(" + path1 + ") ];");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
  }

  @Test
  public void saveEmptyFileArray() throws IOException {
    // given
    script("run : [ ];");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
  }

  @Test
  public void file_array_can_contain_duplicate_values() throws Exception {
    // given
    fileSystem.createFileContainingItsPath(path1);

    script("run : [ file(" + path1 + "), file(" + path1 + ") ] | filter('nothing');\n");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
  }
}
