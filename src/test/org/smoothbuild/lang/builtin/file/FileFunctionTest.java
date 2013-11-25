package org.smoothbuild.lang.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.IoConstants.SMOOTH_DIR;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.file.FileFunction.Parameters;
import org.smoothbuild.lang.builtin.file.err.NoSuchFileButDirError;
import org.smoothbuild.lang.builtin.file.err.IllegalPathError;
import org.smoothbuild.lang.builtin.file.err.NoSuchFileError;
import org.smoothbuild.lang.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.io.fs.base.PathTesting;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.testing.lang.type.FileTester;
import org.smoothbuild.testing.task.exec.FakePluginApi;

public class FileFunctionTest {
  FakePluginApi pluginApi = new FakePluginApi();

  @Test
  public void accessToSmoothDirIsReported() throws Exception {
    try {
      runExecute(params(SMOOTH_DIR.value()));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(ReadFromSmoothDirError.class);
    }
  }

  @Test
  public void accessToSmoothSubDirIsReported() throws Exception {
    try {
      runExecute(params(SMOOTH_DIR.value() + Path.SEPARATOR + "abc"));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(ReadFromSmoothDirError.class);
    }
  }

  @Test
  public void illegalPathIsReported() {
    for (String path : PathTesting.listOfInvalidPaths()) {
      pluginApi = new FakePluginApi();
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
      assertThat(e.errorMessage()).isInstanceOf(NoSuchFileError.class);
    }
  }

  @Test
  public void nonFilePathIsReported() throws Exception {
    Path dir = path("some/path");
    Path file = dir.append(path("file.txt"));
    pluginApi.projectFileSystem().createFileContainingItsPath(file);

    try {
      runExecute(params(dir.value()));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(NoSuchFileButDirError.class);
    }
  }

  @Test
  public void execute() throws Exception {
    Path filePath = path("file/path/file.txt");
    pluginApi.projectFileSystem().createFileContainingItsPath(filePath);

    SFile file = runExecute(params(filePath.value()));

    assertThat(file.path()).isEqualTo(filePath);
    FileTester.assertContentContainsFilePath(file);
  }

  private static FileFunction.Parameters params(final String path) {
    return new FileFunction.Parameters() {
      @Override
      public SString path() {
        return new FakeString(path);
      }
    };
  }

  private SFile runExecute(Parameters params) {
    return FileFunction.execute(pluginApi, params);
  }
}
