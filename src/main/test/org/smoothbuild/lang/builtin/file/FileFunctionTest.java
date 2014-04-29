package org.smoothbuild.lang.builtin.file;

import static org.junit.Assert.fail;
import static org.smoothbuild.SmoothContants.SMOOTH_DIR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchFileButDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.builtin.BuiltinSmoothModule;
import org.smoothbuild.lang.builtin.file.err.IllegalPathError;
import org.smoothbuild.lang.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.PathTesting;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

public class FileFunctionTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private FakeNativeApi nativeApi = new FakeNativeApi();
  private final Path path = path("file/path/file.txt");

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
  public void illegalPathIsReported() {
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
      runExecute(params("some/path/file.txt"));
      fail("exception should be thrown");
    } catch (NoSuchFileError e) {
      // expected
    }
  }

  @Test
  public void nonFilePathIsReported() throws Exception {
    Path dir = path("some/path");
    Path file = dir.append(path("file.txt"));
    nativeApi.projectFileSystem().createFileContainingItsPath(file);

    try {
      runExecute(params(dir.value()));
      fail("exception should be thrown");
    } catch (NoSuchFileButDirError e) {
      // expected
    }
  }

  @Test
  public void execute() throws Exception {
    given(nativeApi.projectFileSystem()).createFileContainingItsPath(path);
    when(runExecute(params(path.value())));
    thenReturned(objectsDb.file(path));
  }

  private BuiltinSmoothModule.FileParameters params(final String path) {
    return new BuiltinSmoothModule.FileParameters() {
      @Override
      public SString path() {
        return objectsDb.string(path);
      }
    };
  }

  private SFile runExecute(BuiltinSmoothModule.FileParameters params) {
    return FileFunction.execute(nativeApi, params);
  }
}
