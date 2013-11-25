package org.smoothbuild.lang.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.IoConstants.SMOOTH_DIR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.lang.type.FileArrayMatchers.containsFileContainingItsPath;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.file.FilesFunction.Parameters;
import org.smoothbuild.lang.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.lang.builtin.file.err.NoSuchDirButFileError;
import org.smoothbuild.lang.builtin.file.err.IllegalPathError;
import org.smoothbuild.lang.builtin.file.err.NoSuchDirError;
import org.smoothbuild.lang.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.io.fs.base.PathTesting;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.testing.task.exec.FakePluginApi;

public class FilesFunctionTest {
  FakePluginApi pluginApi = new FakePluginApi();

  @Test
  public void listingFilesFromRootDirIsForbidden() throws Exception {
    try {
      runExecute(params(Path.rootPath().value()));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(CannotListRootDirError.class);
    }
  }

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
  public void illegalPathsAreReported() {
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
      runExecute(params("some/path"));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(NoSuchDirError.class);
    }
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    Path filePath = path("some/path/file.txt");
    pluginApi.projectFileSystem().createFileContainingItsPath(filePath);

    try {
      runExecute(params(filePath.value()));
      fail("exception should be thrown");

    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(NoSuchDirButFileError.class);
    }
  }

  @Test
  public void execute() throws Exception {
    Path rootPath = path("root/path");
    Path filePath = path("file/path/file.txt");
    pluginApi.projectFileSystem().subFileSystem(rootPath).createFileContainingItsPath(filePath);

    SArray<SFile> fileArray = runExecute(params(rootPath.value()));

    assertThat(containsFileContainingItsPath(filePath).matches(fileArray));
  }

  private static FilesFunction.Parameters params(final String dir) {
    return new FilesFunction.Parameters() {
      @Override
      public SString dir() {
        return new FakeString(dir);
      }
    };
  }

  private SArray<SFile> runExecute(Parameters params) {
    return FilesFunction.execute(pluginApi, params);
  }
}
