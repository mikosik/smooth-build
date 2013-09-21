package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.plugin.api.Path.rootPath;

import org.junit.Test;
import org.smoothbuild.builtin.file.SaveFunction.Parameters;
import org.smoothbuild.builtin.file.err.EitherFileOrFilesMustBeProvidedError;
import org.smoothbuild.builtin.file.err.FileAndFilesSpecifiedError;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.PathIsNotADirError;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.PathTest;
import org.smoothbuild.plugin.api.PluginErrorException;
import org.smoothbuild.plugin.internal.StoredFile;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.plugin.internal.TestFile;
import org.smoothbuild.testing.plugin.internal.TestFileSet;
import org.smoothbuild.testing.plugin.internal.TestSandbox;

public class SaveFunctionTest {
  TestSandbox sandbox = new TestSandbox();
  TestFileSystem fileSystem = sandbox.projectFileSystem();

  @Test
  public void missingFileAndFileSetAreReported() throws Exception {
    try {
      runExecute(params(null, null, "my/path"));
      fail("exception should be thrown");
    } catch (PluginErrorException e) {
      // expected
      assertThat(e.error()).isInstanceOf(EitherFileOrFilesMustBeProvidedError.class);
    }
  }

  @Test
  public void specifyingBotFileAndFileSetIsReported() throws Exception {
    try {
      runExecute(params(mock(File.class), mock(FileSet.class), "my/path"));
      fail("exception should be thrown");
    } catch (PluginErrorException e) {
      // expected
      assertThat(e.error()).isInstanceOf(FileAndFilesSpecifiedError.class);
    }
  }

  @Test
  public void illegalPathsAreReported() {
    for (String path : PathTest.listOfInvalidPaths()) {
      sandbox = new TestSandbox();
      try {
        runExecute(params(mock(File.class), path));
        fail("exception should be thrown");
      } catch (PluginErrorException e) {
        // expected
        assertThat(e.error()).isInstanceOf(IllegalPathError.class);
      }
    }
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    Path file = path("some/path/file.txt");
    fileSystem.createEmptyFile(file);

    try {
      runExecute(params(mock(File.class), file.value()));
      fail("exception should be thrown");
    } catch (PluginErrorException e) {
      // expected
      assertThat(e.error()).isInstanceOf(PathIsNotADirError.class);
    }
  }

  @Test
  public void nonDirPathAfterMergingDirAndFileIsReported() throws Exception {
    Path destinationDir = path("root/path/");
    Path filePath = path("file/path/file.txt");
    fileSystem.createEmptyFile(path("root/path/file/path"));

    StoredFile file = new StoredFile(new SubFileSystem(fileSystem, rootPath()), filePath);

    try {
      runExecute(params(file, destinationDir.value()));
      fail("exception should be thrown");
    } catch (PluginErrorException e) {
      // expected
      assertThat(e.error()).isInstanceOf(PathIsNotADirError.class);
    }
  }

  @Test
  public void executeWithFile() throws Exception {
    // given
    Path dir = path("destination/path");
    Path path = path("file/path/file.txt");
    TestFile file = fileSystem.createFileContainingItsPath(path);

    // when
    runExecute(params(file, dir.value()));

    // then
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(path);
  }

  @Test
  public void executeWithFileSet() throws Exception {
    // given
    Path destinationDir = path("destination/path");
    Path path1 = path("file/path/file1.txt");
    Path path2 = path("file/path/file2.txt");

    TestFileSet sourceFileSet = new TestFileSet(fileSystem);
    sourceFileSet.createFile(path1).createContentWithFilePath();
    sourceFileSet.createFile(path2).createContentWithFilePath();

    // when
    runExecute(params(sourceFileSet, destinationDir.value()));

    // then
    TestFileSystem subFileSystem = fileSystem.subFileSystem(destinationDir);
    subFileSystem.assertFileContainsItsPath(path1);
    subFileSystem.assertFileContainsItsPath(path2);
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
