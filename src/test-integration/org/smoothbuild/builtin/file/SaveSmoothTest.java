package org.smoothbuild.builtin.file;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
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
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.fs.base.TestPath;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.testing.type.impl.TestFileSet;

public class SaveSmoothTest extends IntegrationTestCase {

  // successful execution

  @Test
  public void executeWithFile() throws Exception {
    // given
    Path dirPath = path("output");
    TestFile file = file(path("def/filename.txt"));
    file.createContentWithFilePath();
    script("run : file(" + file.path() + ") | save(" + dirPath + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
    fileSet(dirPath).file(file.path()).assertContentContainsFilePath();
  }

  @Test
  public void executeWithFileSet() throws Exception {
    // given
    Path dir = path("output");
    TestFile file = file(path("def/filename.txt"));
    TestFile file2 = file(path("def/filename2.txt"));
    file.createContentWithFilePath();
    file2.createContentWithFilePath();

    script("run : [ file(" + file.path() + "), file(" + file2.path() + ") ] | save(" + dir + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
    TestFileSet outputFiles = fileSet(dir);
    outputFiles.file(file.path()).assertContentContainsFilePath();
    outputFiles.file(file2.path()).assertContentContainsFilePath();
  }

  // file/files param validation

  @Test
  public void missingFileAndFileSetAreReported() throws Exception {
    // given
    script("run : save(" + path("output") + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(EitherFileOrFilesMustBeProvidedError.class);
  }

  @Test
  public void specifyingBotFileAndFileSetIsReported() throws Exception {
    // given
    TestFile file = file(path("def/filename.txt"));
    TestFile file2 = file(path("def/filename2.txt"));
    file.createContentWithFilePath();
    file2.createContentWithFilePath();

    script("run : [ file(" + file.path() + ") ] | save(file=file(" + file2.path() + "), "
        + path("output") + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(FileAndFilesSpecifiedError.class);
  }

  // dir param validation

  @Test
  public void missingDirIsReported() throws IOException {
    // given
    TestFile file = file(path("def/filename.txt"));
    file.createContentWithFilePath();

    script("run : file(" + file.path() + ") | save();");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(MissingRequiredArgsError.class);
  }

  @Test
  public void illegalPathsAreReported() throws Exception {
    for (String path : TestPath.listOfInvalidPaths()) {
      reset();

      // given
      TestFile file = file(path("def/filename.txt"));
      file.createContentWithFilePath();

      script("run : file(" + file.path() + ") | save('" + path + "');");

      // when
      smoothApp.run("run");

      // then
      messages.assertOnlyProblem(IllegalPathError.class);
    }
  }

  @Test
  public void dirEqualSmoothDirIsReported() throws IOException {
    // given
    TestFile file = file(path("def/filename.txt"));
    file.createContentWithFilePath();

    script("run : file(" + file.path() + ") | save(" + BUILD_DIR + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(WriteToSmoothDirError.class);
  }

  @Test
  public void dirStartingWithSmoothDirIsReported() throws IOException {
    // given
    TestFile file = file(path("def/filename.txt"));
    file.createContentWithFilePath();

    script("run : file(" + file.path() + ") | save(" + BUILD_DIR.append(path("abc")) + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(WriteToSmoothDirError.class);
  }

  @Test
  public void filePassedAsDirParamIsReported() throws Exception {
    // given
    TestFile file = file(path("def/filename.txt"));
    TestFile file2 = file(path("def/filename2.txt"));
    file.createContentWithFilePath();
    file2.createContentWithFilePath();

    script("run : file(" + file.path() + ") | save(" + file2.path() + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(DirParamIsAFileError.class);
  }

  @Test
  public void dirParamContainingFileInsideItsPathIsReported() throws Exception {
    // given
    TestFile file = file(path("def/filename.txt"));
    TestFile file2 = file(path("def/filename2.txt"));
    file.createContentWithFilePath();
    file2.createContentWithFilePath();

    script("run : file(" + file.path() + ") | save(" + file2.path().append(path("abc")) + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(DirParamSubdirIsAFileError.class);
  }

  // validating dir+file path

  @Test
  public void rootDirAndFileHavingSmoothDirButNotAtBeginningIsOk() throws IOException {
    // given
    TestFile file = file(path("abc").append(BUILD_DIR));
    file.createContentWithFilePath();

    script("run : file(" + file.path() + ") | save(" + Path.rootPath() + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
  }

  @Test
  public void concateatedDirAndFileEqualToExistingDirPathIsReported() throws Exception {
    // given
    Path dir = path("abc");
    TestFile file = file(path("def/filename.txt"));
    TestFile file2 = file(dir.append(file.path()).append(path("abc")));
    file.createContentWithFilePath();
    file2.createContentWithFilePath();

    script("run : file(" + file.path() + ") | save(" + dir + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(FileOutputIsADirError.class);
  }

  @Test
  public void concatenatedDirAndFileContainingFileInsideItsPathIsReported() throws Exception {
    // given
    TestFile file = file(path("def/filename.txt"));
    file.createContentWithFilePath();
    file(path("abc/def")).createContentWithFilePath();

    script("run : file(" + file.path() + ") | save('abc');");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(FileOutputSubdirIsAFileError.class);
  }

}
