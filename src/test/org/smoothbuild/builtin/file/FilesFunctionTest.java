package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.plugin.FileImplTest.assertContentHasFilePath;
import static org.smoothbuild.plugin.Path.path;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.builtin.file.FilesFunction.Parameters;
import org.smoothbuild.builtin.file.exc.IllegalPathException;
import org.smoothbuild.builtin.file.exc.NoSuchPathException;
import org.smoothbuild.builtin.file.exc.PathIsNotADirException;
import org.smoothbuild.plugin.FileList;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.PathTest;
import org.smoothbuild.plugin.TestingSandbox;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.plugin.exc.MissingArgException;
import org.smoothbuild.plugin.exc.ParamException;

public class FilesFunctionTest {
  TestingSandbox sandbox = new TestingSandbox();

  @Test
  public void missingDirArgIsReported() throws Exception {
    try {
      runExecute(params(null));
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
        runExecute(params(path));
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
      runExecute(params("some/path"));
      Assert.fail("exception should be thrown");
    } catch (NoSuchPathException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    Path filePath = path("some/path/file.txt");
    sandbox.fileSystem().createEmptyFile(filePath);
    try {
      runExecute(params(filePath.value()));
      Assert.fail("exception should be thrown");
    } catch (PathIsNotADirException e) {
      // expected

      assertExceptionContainsDirParam(e);
    }
  }

  @Test
  public void execute() throws Exception {
    Path rootPath = path("root/path");
    Path filePath = path("file/path/file.txt");
    sandbox.fileSystem().createFileContainingItsPath(rootPath, filePath);

    FileList fileList = runExecute(params(rootPath.value()));

    assertContentHasFilePath(fileList.file(filePath));
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

  private FileList runExecute(Parameters params) throws FunctionException {
    return FilesFunction.execute(sandbox, params);
  }
}
