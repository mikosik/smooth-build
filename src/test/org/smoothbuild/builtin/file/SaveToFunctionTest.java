package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.plugin.Path.rootPath;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.builtin.file.SaveToFunction.Parameters;
import org.smoothbuild.builtin.file.exc.IllegalPathException;
import org.smoothbuild.builtin.file.exc.PathIsNotADirException;
import org.smoothbuild.fs.plugin.FileImpl;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.PathTest;
import org.smoothbuild.plugin.TestingSandbox;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.plugin.exc.MissingArgException;
import org.smoothbuild.plugin.exc.ParamException;
import org.smoothbuild.testing.TestingFileSystem;

public class SaveToFunctionTest {
  TestingSandbox sandbox = new TestingSandbox();
  TestingFileSystem fileSystem = sandbox.fileSystem();

  @Test
  public void missingDirArgIsReported() throws Exception {
    try {
      runExecute(params(mock(File.class), null));
      Assert.fail("exception should be thrown");
    } catch (MissingArgException e) {
      // expected

      assertExceptionContainsDirParamName(e);
    }
  }

  @Test
  public void illegalPathsAreReported() throws FunctionException {
    for (String path : PathTest.listOfInvalidPaths()) {
      try {
        runExecute(params(mock(File.class), path));
        Assert.fail("exception should be thrown");
      } catch (IllegalPathException e) {
        // expected

        assertExceptionContainsDirParamName(e);
      }
    }
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    Path file = path("some/path/file.txt");
    fileSystem.createEmptyFile(file);

    try {
      runExecute(params(mock(File.class), file.value()));
      Assert.fail("exception should be thrown");
    } catch (PathIsNotADirException e) {
      // expected

      assertExceptionContainsDirParamName(e);
    }
  }

  @Test
  public void nonDirPathAfterMergingDirAndFileIsReported() throws Exception {
    Path destinationDir = path("root/path/");
    Path filePath = path("file/path/file.txt");
    fileSystem.createEmptyFile(path("root/path/file/path"));

    FileImpl file = new FileImpl(fileSystem, rootPath(), filePath);

    try {
      runExecute(params(file, destinationDir.value()));
      Assert.fail("exception should be thrown");
    } catch (PathIsNotADirException e) {
      // expected

      assertExceptionContainsDirParamName(e);
    }
  }

  @Test
  public void execute() throws Exception {
    Path destinationDir = path("root/path");
    Path root = path("file/root");
    Path path = path("file/path/file.txt");

    fileSystem.createFileContainingItsPath(root, path);
    FileImpl file = new FileImpl(fileSystem, root, path);

    runExecute(params(file, destinationDir.value()));

    fileSystem.assertFileContainsItsPath(destinationDir, path);
  }

  private void assertExceptionContainsDirParamName(ParamException e) {
    assertThat(e.paramName()).isSameAs("dir");
  }

  private static Parameters params(final File file, final String dir) {
    return new SaveToFunction.Parameters() {
      @Override
      public File file() {
        return file;
      }

      @Override
      public String dir() {
        return dir;
      }
    };
  }

  private void runExecute(Parameters params) throws FunctionException {
    SaveToFunction.execute(sandbox, params);
  }
}
