package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.PathUtils.WORKING_DIR;
import static org.smoothbuild.lang.type.Path.path;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.fs.base.PathUtilsTest;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.IllegalPathException;
import org.smoothbuild.lang.function.exc.MissingArgException;
import org.smoothbuild.lang.function.exc.ParamException;
import org.smoothbuild.lang.function.exc.PathIsNotADirException;
import org.smoothbuild.lang.internal.FileRoImpl;
import org.smoothbuild.lang.internal.FileRwImpl;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.testing.TestingFileSystem;

public class SaveToFunctionTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  SaveToFunction function = new SaveToFunction(fileSystem);

  @SuppressWarnings("unchecked")
  Param<FileRo> fileParam = (Param<FileRo>) function.params().param("file");
  @SuppressWarnings("unchecked")
  Param<String> dirParam = (Param<String>) function.params().param("dir");

  @Test
  public void missingDirArgIsReported() throws Exception {
    fileParam.set(Mockito.mock(FileRo.class));

    try {
      function.execute();
      Assert.fail("exception should be thrown");
    } catch (MissingArgException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void illegalPathsAreReported() throws FunctionException {
    fileParam.set(Mockito.mock(FileRo.class));

    for (String path : PathUtilsTest.listOfInvalidPaths()) {
      dirParam.set(path);
      try {
        function.execute();
        Assert.fail("exception should be thrown");
      } catch (IllegalPathException e) {
        // expected

        assertExceptionContainsDirParam(e);
      }
    }
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    fileParam.set(Mockito.mock(FileRo.class));

    String filePath = "some/path/file.txt";
    fileSystem.createEmptyFile(filePath);
    dirParam.set(filePath);
    try {
      function.execute();
      Assert.fail("exception should be thrown");
    } catch (PathIsNotADirException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void nonDirPathAfterMergingDirAndFileIsReported() throws Exception {
    String destinationDirPath = "root/path/";
    String fileRootPath = "file/path";
    String filePath = fileRootPath + "/file.txt";
    fileSystem.createEmptyFile(destinationDirPath + fileRootPath);

    dirParam.set(destinationDirPath);
    fileParam.set(new FileRoImpl(fileSystem, path(WORKING_DIR), path(filePath)));

    try {
      function.execute();
      Assert.fail("exception should be thrown");
    } catch (PathIsNotADirException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void execute() throws Exception {
    String destinationDirPath = "root/path";
    String fileRoot = "file/root";
    String filePath = "file/path/file.txt";

    dirParam.set(destinationDirPath);
    fileSystem.createFile(fileRoot, filePath);
    fileParam.set(new FileRwImpl(fileSystem, path(fileRoot), path(filePath)));

    function.execute();

    fileSystem.assertContentHasFilePath(destinationDirPath, filePath);
  }

  private void assertExceptionContainsDirParam(ParamException e) {
    @SuppressWarnings("unchecked")
    Param<String> param = (Param<String>) e.param();
    assertThat(param).isSameAs(dirParam);
  }
}
