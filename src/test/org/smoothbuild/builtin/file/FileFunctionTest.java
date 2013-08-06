package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.internal.FileRoImplTest.assertContentHasFilePath;
import static org.smoothbuild.lang.type.Path.path;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.IllegalPathException;
import org.smoothbuild.lang.function.exc.MissingArgException;
import org.smoothbuild.lang.function.exc.NoSuchPathException;
import org.smoothbuild.lang.function.exc.ParamException;
import org.smoothbuild.lang.function.exc.PathIsNotAFileException;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.PathTest;
import org.smoothbuild.testing.TestingFileSystem;

public class FileFunctionTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  FileFunction fileFunction = new FileFunction(fileSystem);

  @SuppressWarnings("unchecked")
  Param<String> pathParam = (Param<String>) fileFunction.params().param("path");

  @Test
  public void missingPathArgIsReported() throws Exception {
    try {
      fileFunction.execute();
      Assert.fail("exception should be thrown");
    } catch (MissingArgException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void illegalPathIsReported() throws FunctionException {
    for (String path : PathTest.listOfInvalidPaths()) {
      pathParam.set(path);
      try {
        fileFunction.execute();
        Assert.fail("exception should be thrown");
      } catch (IllegalPathException e) {
        // expected

        assertExceptionContainsDirParam(e);
      }
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {
    pathParam.set("some/path/file.txt");
    try {
      fileFunction.execute();
      Assert.fail("exception should be thrown");
    } catch (NoSuchPathException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void nonFilePathIsReported() throws Exception {
    String dirPath = "some/path/";
    String filePath = dirPath + "file.txt";
    fileSystem.createEmptyFile(filePath);

    pathParam.set(dirPath);
    try {
      fileFunction.execute();
      Assert.fail("exception should be thrown");
    } catch (PathIsNotAFileException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void execute() throws Exception {
    String filePath = "file/path/file.txt";
    fileSystem.createFile(".", filePath);
    pathParam.set(filePath);

    FileRo fileRo = fileFunction.execute();

    assertThat(fileRo.path()).isEqualTo(path(filePath));
    assertContentHasFilePath(fileRo);
  }

  private void assertExceptionContainsDirParam(ParamException e) {
    @SuppressWarnings("unchecked")
    Param<String> param = (Param<String>) e.param();
    assertThat(param).isSameAs(pathParam);
  }
}
