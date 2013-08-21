package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.fs.plugin.FileImplTest.assertContentHasFilePath;
import static org.smoothbuild.plugin.Path.path;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.builtin.file.exc.IllegalPathException;
import org.smoothbuild.builtin.file.exc.NoSuchPathException;
import org.smoothbuild.builtin.file.exc.PathIsNotAFileException;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.PathTest;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.plugin.exc.MissingArgException;
import org.smoothbuild.plugin.exc.ParamException;
import org.smoothbuild.testing.TestingFileSystem;

public class FileFunctionTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  FileFunction fileFunction = new FileFunction(fileSystem);

  @Test
  public void missingPathArgIsReported() throws Exception {
    try {
      fileFunction.execute(params(null));
      Assert.fail("exception should be thrown");
    } catch (MissingArgException e) {
      // expected

      assertExceptionContainsParamName(e);
    }
  }

  @Test
  public void illegalPathIsReported() throws FunctionException {
    for (String path : PathTest.listOfInvalidPaths()) {
      try {
        fileFunction.execute(params(path));
        Assert.fail("exception should be thrown");
      } catch (IllegalPathException e) {
        // expected

        assertExceptionContainsParamName(e);
      }
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {
    try {
      fileFunction.execute(params("some/path/file.txt"));
      Assert.fail("exception should be thrown");
    } catch (NoSuchPathException e) {
      // expected

      assertExceptionContainsParamName(e);
    }
  }

  @Test
  public void nonFilePathIsReported() throws Exception {
    String dirPath = "some/path/";
    String filePath = dirPath + "file.txt";
    fileSystem.createEmptyFile(filePath);

    try {
      fileFunction.execute(params(dirPath));
      Assert.fail("exception should be thrown");
    } catch (PathIsNotAFileException e) {
      // expected

      assertExceptionContainsParamName(e);
    }
  }

  @Test
  public void execute() throws Exception {
    String filePath = "file/path/file.txt";
    fileSystem.createFile(".", filePath);

    File file = fileFunction.execute(params(filePath));

    assertThat(file.path()).isEqualTo(path(filePath));
    assertContentHasFilePath(file);
  }

  private void assertExceptionContainsParamName(ParamException e) {
    assertThat(e.paramName()).isSameAs("path");
  }

  private static FileFunction.Parameters params(final String path) {
    return new FileFunction.Parameters() {
      @Override
      public String path() {
        return path;
      }
    };
  }
}
