package org.smoothbuild.builtin.file;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.plugin.api.Path.rootPath;

import org.junit.Test;
import org.smoothbuild.builtin.file.SaveFunction.Parameters;
import org.smoothbuild.builtin.file.err.EitherFileOrFilesMustBeProvidedError;
import org.smoothbuild.builtin.file.err.FileAndFilesSpecifiedError;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.MissingRequiredArgError;
import org.smoothbuild.builtin.file.err.PathIsNotADirError;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.TestingSandbox;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.PathTest;
import org.smoothbuild.plugin.internal.StoredFile;
import org.smoothbuild.testing.TestingFile;
import org.smoothbuild.testing.TestingFileSystem;

public class SaveFunctionTest {
  TestingSandbox sandbox = new TestingSandbox();
  TestingFileSystem fileSystem = sandbox.projectFileSystem();

  @Test
  public void missingDirArgIsReported() throws Exception {
    runExecute(params(mock(File.class), null));
    sandbox.problems().assertOnlyProblem(MissingRequiredArgError.class);
  }

  @Test
  public void missingFileAndFileSetAreReported() throws Exception {
    runExecute(params(null, null, "my/path"));
    sandbox.problems().assertOnlyProblem(EitherFileOrFilesMustBeProvidedError.class);
  }

  @Test
  public void specifyingBotFileAndFileSetIsReported() throws Exception {
    runExecute(params(mock(File.class), mock(FileSet.class), "my/path"));
    sandbox.problems().assertOnlyProblem(FileAndFilesSpecifiedError.class);
  }

  @Test
  public void illegalPathsAreReported() {
    for (String path : PathTest.listOfInvalidPaths()) {
      sandbox = new TestingSandbox();
      runExecute(params(mock(File.class), path));
      sandbox.problems().assertOnlyProblem(IllegalPathError.class);
    }
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    Path file = path("some/path/file.txt");
    fileSystem.createEmptyFile(file);

    runExecute(params(mock(File.class), file.value()));
    sandbox.problems().assertOnlyProblem(PathIsNotADirError.class);
  }

  @Test
  public void nonDirPathAfterMergingDirAndFileIsReported() throws Exception {
    Path destinationDir = path("root/path/");
    Path filePath = path("file/path/file.txt");
    fileSystem.createEmptyFile(path("root/path/file/path"));

    StoredFile file = new StoredFile(new SubFileSystem(fileSystem, rootPath()), filePath);

    runExecute(params(file, destinationDir.value()));
    sandbox.problems().assertOnlyProblem(PathIsNotADirError.class);
  }

  @Test
  public void executeWithFile() throws Exception {
    // given
    Path dir = path("destination/path");
    Path path = path("file/path/file.txt");

    fileSystem.createFileContainingItsPath(path);
    TestingFile file = new TestingFile(fileSystem, path);

    // when
    runExecute(params(file, dir.value()));

    // then
    fileSystem.assertFileContainsItsPath(dir, path);
  }

  @Test
  public void executeWithFileSet() throws Exception {
    // given
    Path destinationDir = path("destination/path");
    Path path1 = path("file/path/file1.txt");
    Path path2 = path("file/path/file2.txt");

    fileSystem.createFileContainingItsPath(path1);
    TestingFile file1 = new TestingFile(fileSystem, path1);
    fileSystem.createFileContainingItsPath(path2);
    TestingFile file2 = new TestingFile(fileSystem, path2);

    MutableFileSet fileSet = new MutableFileSet();
    fileSet.add(file1);
    fileSet.add(file2);

    // when
    runExecute(params(fileSet, destinationDir.value()));

    // then
    fileSystem.assertFileContainsItsPath(destinationDir, path1);
    fileSystem.assertFileContainsItsPath(destinationDir, path2);
  }

  private static Parameters params(final File file, final String dir) {
    return params(file, null, dir);
  }

  private static Parameters params(final FileSet fileSet, final String dir) {
    return params(null, fileSet, dir);
  }

  private static Parameters params(final File file, final FileSet fileSet, final String dir) {
    return new SaveFunction.Parameters() {
      @Override
      public File file() {
        return file;
      }

      @Override
      public FileSet files() {
        return fileSet;
      }

      @Override
      public String dir() {
        return dir;
      }
    };
  }

  private void runExecute(Parameters params) {
    SaveFunction.execute(sandbox, params);
  }
}
