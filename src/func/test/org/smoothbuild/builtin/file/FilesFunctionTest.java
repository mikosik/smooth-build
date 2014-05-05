package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchDirButFileError;
import org.smoothbuild.io.fs.base.err.NoSuchDirError;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.PathTesting;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

public class FilesFunctionTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private FakeNativeApi nativeApi = new FakeNativeApi();

  @Test
  public void listingFilesFromRootDirIsForbidden() throws Exception {
    try {
      runExecute(params(Path.rootPath().value()));
      fail("exception should be thrown");
    } catch (CannotListRootDirError e) {
      // expected
    }
  }

  @Test
  public void accessToSmoothDirIsReported() throws Exception {
    try {
      runExecute(params(SMOOTH_DIR.value()));
      fail("exception should be thrown");
    } catch (ReadFromSmoothDirError e) {
      // expected
    }
  }

  @Test
  public void accessToSmoothSubDirIsReported() throws Exception {
    try {
      runExecute(params(SMOOTH_DIR.value() + Path.SEPARATOR + "abc"));
      fail("exception should be thrown");
    } catch (ReadFromSmoothDirError e) {
      // expected
    }
  }

  @Test
  public void illegalPathsAreReported() {
    for (String path : PathTesting.listOfInvalidPaths()) {
      nativeApi = new FakeNativeApi();
      try {
        runExecute(params(path));
        fail("exception should be thrown");
      } catch (IllegalPathError e) {
        // expected
      }
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {
    try {
      runExecute(params("some/path"));
      fail("exception should be thrown");
    } catch (NoSuchDirError e) {
      // expected
    }
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    Path filePath = path("some/path/file.txt");
    nativeApi.projectFileSystem().createFileContainingItsPath(filePath);

    try {
      runExecute(params(filePath.value()));
      fail("exception should be thrown");
    } catch (NoSuchDirButFileError e) {
      // expected
    }
  }

  @Test
  public void execute() throws Exception {
    Path rootPath = path("root/path");
    Path filePath = path("file/path/file.txt");
    nativeApi.projectFileSystem().subFileSystem(rootPath).createFileContainingItsPath(filePath);

    SArray<SFile> fileArray = runExecute(params(rootPath.value()));

    SFile expectedFile = objectsDb.file(filePath);
    assertThat(fileArray).containsExactly(expectedFile);
  }

  private BuiltinSmoothModule.FilesParameters params(final String dir) {
    return new BuiltinSmoothModule.FilesParameters() {
      @Override
      public SString dir() {
        return objectsDb.string(dir);
      }
    };
  }

  private SArray<SFile> runExecute(BuiltinSmoothModule.FilesParameters params) {
    return FilesFunction.execute(nativeApi, params);
  }
}
