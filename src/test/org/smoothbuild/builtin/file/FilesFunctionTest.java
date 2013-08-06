package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.internal.FileRoImplTest.assertContentHasFilePath;
import static org.smoothbuild.lang.type.Path.path;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.fs.base.PathUtilsTest;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.IllegalPathException;
import org.smoothbuild.lang.function.exc.MissingArgException;
import org.smoothbuild.lang.function.exc.NoSuchPathException;
import org.smoothbuild.lang.function.exc.ParamException;
import org.smoothbuild.lang.function.exc.PathIsNotADirException;
import org.smoothbuild.lang.type.FilesRo;
import org.smoothbuild.testing.TestingFileSystem;

public class FilesFunctionTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  FilesFunction filesFunction = new FilesFunction(fileSystem);

  @SuppressWarnings("unchecked")
  Param<String> dirParam = (Param<String>) filesFunction.params().param("dir");

  @Test
  public void missingDirArgIsReported() throws Exception {
    try {
      filesFunction.execute();
      Assert.fail("exception should be thrown");
    } catch (MissingArgException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void illegalPathsAreReported() throws FunctionException {
    for (String path : PathUtilsTest.listOfInvalidPaths()) {
      dirParam.set(path);
      try {
        filesFunction.execute();
        Assert.fail("exception should be thrown");
      } catch (IllegalPathException e) {
        // expected

        assertExceptionContainsDirParam(e);
      }
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {
    dirParam.set("some/path");
    try {
      filesFunction.execute();
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
    dirParam.set(filePath);
    try {
      filesFunction.execute();
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
    dirParam.set(rootPath);

    FilesRo filesRo = filesFunction.execute();

    assertContentHasFilePath(filesRo.fileRo(path(filePath)));
  }

  private void assertExceptionContainsDirParam(ParamException e) {
    @SuppressWarnings("unchecked")
    Param<String> param = (Param<String>) e.param();
    assertThat(param).isSameAs(dirParam);
  }
}
