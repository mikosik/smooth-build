package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.internal.FileImplTest.assertContentHasFilePath;
import static org.smoothbuild.lang.type.Path.path;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.IllegalPathException;
import org.smoothbuild.lang.function.exc.MissingArgException;
import org.smoothbuild.lang.function.exc.NoSuchPathException;
import org.smoothbuild.lang.function.exc.ParamException;
import org.smoothbuild.lang.function.exc.PathIsNotADirException;
import org.smoothbuild.lang.type.Files;
import org.smoothbuild.lang.type.PathTest;
import org.smoothbuild.testing.TestingFileSystem;

public class FilesFunctionTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  FilesFunction filesFunction = new FilesFunction(fileSystem);

  @Test
  public void missingDirArgIsReported() throws Exception {
    try {
      filesFunction.execute(params(null));
      Assert.fail("exception should be thrown");
    } catch (MissingArgException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void illegalPathsAreReported() throws FunctionException {
    for (String path : PathTest.listOfInvalidPaths()) {
      try {
        filesFunction.execute(params(path));
        Assert.fail("exception should be thrown");
      } catch (IllegalPathException e) {
        // expected

        assertExceptionContainsDirParam(e);
      }
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {
    try {
      filesFunction.execute(params("some/path"));
      Assert.fail("exception should be thrown");
    } catch (NoSuchPathException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    String filePath = "some/path/file.txt";
    fileSystem.createEmptyFile(filePath);
    try {
      filesFunction.execute(params(filePath));
      Assert.fail("exception should be thrown");
    } catch (PathIsNotADirException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void execute() throws Exception {
    String rootPath = "root/path";
    String filePath = "file/path/file.txt";
    fileSystem.createFile(rootPath, filePath);

    Files files = filesFunction.execute(params(rootPath));

    assertContentHasFilePath(files.file(path(filePath)));
  }

  private void assertExceptionContainsDirParam(ParamException e) {
    assertThat(e.paramName()).isSameAs("dir");
  }

  private static FilesFunction.Parameters params(final String dir) {
    return new FilesFunction.Parameters() {
      @Override
      public String dir() {
        return dir;
      }
    };
  }
}
