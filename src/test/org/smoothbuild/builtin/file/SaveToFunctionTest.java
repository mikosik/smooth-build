package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.plugin.Path.rootPath;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.builtin.file.exc.IllegalPathException;
import org.smoothbuild.builtin.file.exc.PathIsNotADirException;
import org.smoothbuild.fs.plugin.FileImpl;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.PathTest;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.plugin.exc.MissingArgException;
import org.smoothbuild.plugin.exc.ParamException;
import org.smoothbuild.testing.TestingFileSystem;

public class SaveToFunctionTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  SaveToFunction function = new SaveToFunction(fileSystem);

  @Test
  public void missingDirArgIsReported() throws Exception {
    try {
      function.execute(params(mock(File.class), null));
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
        function.execute(params(mock(File.class), path));
        Assert.fail("exception should be thrown");
      } catch (IllegalPathException e) {
        // expected

        assertExceptionContainsDirParamName(e);
      }
    }
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    String filePath = "some/path/file.txt";
    fileSystem.createEmptyFile(filePath);

    try {
      function.execute(params(mock(File.class), filePath));
      Assert.fail("exception should be thrown");
    } catch (PathIsNotADirException e) {
      // expected

      assertExceptionContainsDirParamName(e);
    }
  }

  @Test
  public void nonDirPathAfterMergingDirAndFileIsReported() throws Exception {
    String destinationDirPath = "root/path/";
    String fileRootPath = "file/path";
    String filePath = fileRootPath + "/file.txt";
    fileSystem.createEmptyFile(destinationDirPath + fileRootPath);

    FileImpl file = new FileImpl(fileSystem, rootPath(), path(filePath));

    try {
      function.execute(params(file, destinationDirPath));
      Assert.fail("exception should be thrown");
    } catch (PathIsNotADirException e) {
      // expected

      assertExceptionContainsDirParamName(e);
    }
  }

  @Test
  public void execute() throws Exception {
    String destinationDirPath = "root/path";
    String fileRoot = "file/root";
    String filePath = "file/path/file.txt";

    fileSystem.createFile(fileRoot, filePath);
    FileImpl file = new FileImpl(fileSystem, path(fileRoot), path(filePath));

    function.execute(params(file, destinationDirPath));

    fileSystem.assertContentHasFilePath(destinationDirPath, filePath);
  }

  private void assertExceptionContainsDirParamName(ParamException e) {
    assertThat(e.paramName()).isSameAs("dir");
  }

  private static SaveToFunction.Parameters params(final File file, final String dir) {
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
}
