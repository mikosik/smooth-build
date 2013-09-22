package org.smoothbuild.integration.file;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.AccessToSmoothDirError;
import org.smoothbuild.builtin.file.err.DirParamIsAFileError;
import org.smoothbuild.builtin.file.err.DirParamSubdirIsAFileError;
import org.smoothbuild.builtin.file.err.EitherFileOrFilesMustBeProvidedError;
import org.smoothbuild.builtin.file.err.FileAndFilesSpecifiedError;
import org.smoothbuild.builtin.file.err.FileOutputIsADirError;
import org.smoothbuild.builtin.file.err.FileOutputSubdirIsAFileError;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.parse.def.err.MissingRequiredArgsError;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.PathTest;

public class SaveSmoothTest extends IntegrationTestCase {

  // successful execution

  @Test
  public void executeWithFile() throws Exception {
    // given
    Path dir = path("output");
    Path file = path("def/filename.txt");
    fileSystem.createFileContainingItsPath(file);
    script("run : file(" + file + ") | save(" + dir + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(file);
  }

  @Test
  public void executeWithFileSet() throws Exception {
    // given
    Path dir = path("output");
    Path file = path("def/filename.txt");
    fileSystem.createFileContainingItsPath(file);
    Path file2 = path("def/filename2.txt");
    fileSystem.createFileContainingItsPath(file2);
    script("run : [ file(" + file + "), file(" + file2 + ") ] | save(" + dir + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(file);
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(file2);
  }

  // file/files param validation

  @Test
  public void missingFileAndFileSetAreReported() throws Exception {
    // given
    script("run : save(" + path("output") + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(EitherFileOrFilesMustBeProvidedError.class);
  }

  @Test
  public void specifyingBotFileAndFileSetIsReported() throws Exception {
    // given
    Path file = path("file/path/file.txt");
    Path file2 = path("file/path/file2.txt");
    fileSystem.createFileContainingItsPath(file);
    fileSystem.createFileContainingItsPath(file2);

    script("run : [ file(" + file + ") ] | save(file=file(" + file2 + "), " + path("output") + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(FileAndFilesSpecifiedError.class);
  }

  // dir param validation

  @Test
  public void missingDirIsReported() throws IOException {
    // given
    Path file = path("file/path/file.txt");
    fileSystem.createFileContainingItsPath(file);

    script("run : file(" + file + ") | save();");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(MissingRequiredArgsError.class);
  }

  @Test
  public void illegalPathsAreReported() throws Exception {
    for (String path : PathTest.listOfInvalidPaths()) {
      reset();

      // given
      Path file = path("file/path/file.txt");
      fileSystem.createFileContainingItsPath(file);

      script("run : file(" + file + ") | save('" + path + "');");

      // when
      smoothRunner.run("run");

      // then
      messages.assertOnlyProblem(IllegalPathError.class);
    }
  }

  @Test
  public void dirEqualSmoothDirIsReported() throws IOException {
    // given
    Path file = path("file/path/file.txt");
    fileSystem.createFileContainingItsPath(file);

    script("run : file(" + file + ") | save(" + BUILD_DIR + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(AccessToSmoothDirError.class);
  }

  @Test
  public void dirStartingWithSmoothDirIsReported() throws IOException {
    // given
    Path file = path("file/path/file.txt");
    fileSystem.createFileContainingItsPath(file);

    script("run : file(" + file + ") | save(" + BUILD_DIR.append(path("abc")) + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(AccessToSmoothDirError.class);
  }

  @Test
  public void filePassedAsDirParamIsReported() throws Exception {
    // given
    Path file = path("file/path/file.txt");
    Path file2 = path("file/path/file2.txt");
    fileSystem.createFileContainingItsPath(file);
    fileSystem.createFileContainingItsPath(file2);

    script("run : file(" + file + ") | save(" + file2 + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(DirParamIsAFileError.class);
  }

  @Test
  public void dirParamContainingFileInsideItsPathIsReported() throws Exception {
    // given
    Path file = path("file/path/file.txt");
    Path file2 = path("file/path/file2.txt");
    fileSystem.createFileContainingItsPath(file);
    fileSystem.createFileContainingItsPath(file2);

    script("run : file(" + file + ") | save(" + file2.append(path("abc")) + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(DirParamSubdirIsAFileError.class);
  }

  // validating dir+file path

  @Test
  public void rootDirAndFileHavingSmoothDirButNotAtBeginningIsOk() throws IOException {
    // given
    Path file = path("abc").append(BUILD_DIR);
    fileSystem.createFileContainingItsPath(file);

    script("run : file(" + file + ") | save(" + Path.rootPath() + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
  }

  @Test
  public void concateatedDirAndFileEqualToExistingDirPathIsReported() throws Exception {
    // given
    Path dir = path("abc");
    Path file = path("def/filename.txt");
    Path file2 = dir.append(file).append(path("abc"));

    fileSystem.createFileContainingItsPath(file);
    fileSystem.createFileContainingItsPath(file2);

    script("run : file(" + file + ") | save(" + dir + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(FileOutputIsADirError.class);
  }

  @Test
  public void concateatedDirAndFileContainingFileInsideItsPathIsReported() throws Exception {
    // given
    Path file = path("def/filename.txt");
    fileSystem.createFileContainingItsPath(file);
    fileSystem.createFileContainingItsPath(path("abc/def"));

    script("run : file(" + file + ") | save('abc');");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(FileOutputSubdirIsAFileError.class);
  }

}
