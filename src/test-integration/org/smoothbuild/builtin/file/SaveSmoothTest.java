package org.smoothbuild.builtin.file;

import static org.smoothbuild.fs.FileSystemModule.SMOOTH_DIR;
import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.DirParamIsAFileError;
import org.smoothbuild.builtin.file.err.DirParamSubdirIsAFileError;
import org.smoothbuild.builtin.file.err.EitherFileOrFilesMustBeProvidedError;
import org.smoothbuild.builtin.file.err.FileAndFilesSpecifiedError;
import org.smoothbuild.builtin.file.err.FileOutputIsADirError;
import org.smoothbuild.builtin.file.err.FileOutputSubdirIsAFileError;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.WriteToSmoothDirError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.function.def.args.err.MissingRequiredArgsError;
import org.smoothbuild.testing.fs.base.PathTesting;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class SaveSmoothTest extends IntegrationTestCase {
  Path path = path("def/filename.txt");
  Path path2 = path("def/filename2.txt");

  // successful execution

  @Test
  public void executeWithFile() throws Exception {
    // given
    Path dirPath = path("output");
    Path path = path("def/filename.txt");
    fileSystem.createFileContainingItsPath(path);

    script("run : file(" + path + ") | save(" + dirPath + ");");

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContainsItsPath(dirPath, path);
  }

  @Test
  public void executeWithFileSet() throws Exception {
    // given
    Path dir = path("output");
    fileSystem.createFileContainingItsPath(path);
    fileSystem.createFileContainingItsPath(path2);

    script("run : [ file(" + path + "), file(" + path2 + ") ] | save(" + dir + ");");

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContainsItsPath(dir, path);
    fileSystem.assertFileContainsItsPath(dir, path2);
  }

  // file/files param validation

  @Test
  public void missingFileAndFileSetAreReported() throws Exception {
    // given
    script("run : save(" + path("output") + ");");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(EitherFileOrFilesMustBeProvidedError.class);
  }

  @Test
  public void specifyingBotFileAndFileSetIsReported() throws Exception {
    // given
    fileSystem.createFileContainingItsPath(path);
    fileSystem.createFileContainingItsPath(path2);

    script("run : [ file(" + path + ") ] | save(file=file(" + path2 + "), " + path("output") + ");");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(FileAndFilesSpecifiedError.class);
  }

  // dir param validation

  @Test
  public void missingDirIsReported() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path);

    script("run : file(" + path + ") | save();");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(MissingRequiredArgsError.class);
  }

  @Test
  public void illegalPathsAreReported() throws Exception {
    String destinationPath = PathTesting.listOfInvalidPaths().get(0);

    // given
    fileSystem.createFileContainingItsPath(path);

    script("run : file(" + path + ") | save('" + destinationPath + "');");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(IllegalPathError.class);
  }

  @Test
  public void dirEqualSmoothDirIsReported() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path);

    script("run : file(" + path + ") | save(" + SMOOTH_DIR + ");");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(WriteToSmoothDirError.class);
  }

  @Test
  public void dirStartingWithSmoothDirIsReported() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path);

    script("run : file(" + path + ") | save(" + SMOOTH_DIR.append(path("abc")) + ");");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(WriteToSmoothDirError.class);
  }

  @Test
  public void filePassedAsDirParamIsReported() throws Exception {
    // given
    fileSystem.createFileContainingItsPath(path);
    fileSystem.createFileContainingItsPath(path2);

    script("run : file(" + path + ") | save(" + path2 + ");");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(DirParamIsAFileError.class);
  }

  @Test
  public void dirParamContainingFileInsideItsPathIsReported() throws Exception {
    // given
    fileSystem.createFileContainingItsPath(path);
    fileSystem.createFileContainingItsPath(path2);

    script("run : file(" + path + ") | save(" + path2.append(path("abc")) + ");");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(DirParamSubdirIsAFileError.class);
  }

  // validating dir+file path

  @Test
  public void rootDirAndFileHavingSmoothDirButNotAtBeginningIsOk() throws IOException {
    // given
    path = path("abc").append(SMOOTH_DIR);
    fileSystem.createFileContainingItsPath(path);

    script("run : file(" + path + ") | save(" + Path.rootPath() + ");");

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
  }

  @Test
  public void concateatedDirAndFileEqualToExistingDirPathIsReported() throws Exception {
    // given
    Path dir = path("abc");
    path2 = dir.append(path).append(path("abc"));

    fileSystem.createFileContainingItsPath(path);
    fileSystem.createFileContainingItsPath(path2);

    script("run : file(" + path + ") | save(" + dir + ");");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(FileOutputIsADirError.class);
  }

  @Test
  public void concatenatedDirAndFileContainingFileInsideItsPathIsReported() throws Exception {
    // given
    fileSystem.createFileContainingItsPath(path);
    fileSystem.createFileContainingItsPath(path("abc/def"));

    script("run : file(" + path + ") | save('abc');");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(FileOutputSubdirIsAFileError.class);
  }

}
