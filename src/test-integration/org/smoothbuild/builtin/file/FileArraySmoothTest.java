package org.smoothbuild.builtin.file;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.ARTIFACTS_PATH;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class FileArraySmoothTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  Path path1 = path("file/path/file1.txt");
  Path path2 = path("file/path/file2.txt");

  @Test
  public void arrayWithTrailingCommaIsAllowed() throws Exception {
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    script(fileSystem, "run : [ file(" + path1 + "), file(" + path2 + "), ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

  @Test
  public void arrayWithOnlyCommaIsForbidden() throws Exception {
    // given
    script(fileSystem, "run : [ , ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(SyntaxError.class);
  }

  @Test
  public void saveFileArrayWithTwoFiles() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    script(fileSystem, "run : [ file(" + path1 + "), file(" + path2 + ") ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

  @Test
  public void saveFileArrayWithOneFile() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path1);

    script(fileSystem, "run : [ file(" + path1 + ") ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
  }

  @Test
  public void saveEmptyFileArray() throws IOException {
    // given
    script(fileSystem, "run : [ ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
  }

  @Test
  public void file_array_can_contain_duplicate_values() throws Exception {
    // given
    fileSystem.createFileContainingItsPath(path1);

    script(fileSystem, "run : [ file(" + path1 + "), file(" + path1 + ") ] | filter('nothing');\n");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
  }
}
