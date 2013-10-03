package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.builtin.file.FileFunction.Parameters;
import org.smoothbuild.builtin.file.err.FileParamIsADirError;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.testing.fs.base.TestPath;
import org.smoothbuild.testing.task.TestSandbox;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.type.api.File;

public class FileFunctionTest {
  TestSandbox sandbox = new TestSandbox();

  @Test
  public void accessToSmoothDirIsReported() throws Exception {
    try {
      runExecute(params(BUILD_DIR.value()));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(ReadFromSmoothDirError.class);
    }
  }

  @Test
  public void accessToSmoothSubDirIsReported() throws Exception {
    try {
      runExecute(params(BUILD_DIR.append(path("abc")).value()));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(ReadFromSmoothDirError.class);
    }
  }

  @Test
  public void illegalPathIsReported() {
    for (String path : TestPath.listOfInvalidPaths()) {
      sandbox = new TestSandbox();
      try {
        runExecute(params(path));
        fail("exception should be thrown");
      } catch (ErrorMessageException e) {
        // expected
        assertThat(e.errorMessage()).isInstanceOf(IllegalPathError.class);
      }
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {

    try {
      runExecute(params("some/path/file.txt"));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(NoSuchPathError.class);
    }
  }

  @Test
  public void nonFilePathIsReported() throws Exception {
    Path dir = path("some/path");
    Path file = dir.append(path("file.txt"));
    sandbox.projectFileSystem().createEmptyFile(file);

    try {
      runExecute(params(dir.value()));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(FileParamIsADirError.class);
    }
  }

  @Test
  public void execute() throws Exception {
    Path filePath = path("file/path/file.txt");
    sandbox.projectFileSystem().createFileContainingItsPath(filePath);

    File file = runExecute(params(filePath.value()));

    assertThat(file.path()).isEqualTo(filePath);
    FileTester.assertContentContainsFilePath(file);
  }

  private static FileFunction.Parameters params(final String path) {
    return new FileFunction.Parameters() {
      @Override
      public String path() {
        return path;
      }
    };
  }

  private File runExecute(Parameters params) {
    return FileFunction.execute(sandbox, params);
  }
}
