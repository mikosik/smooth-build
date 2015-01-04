package org.smoothbuild.builtin.file;

import static org.junit.Assert.fail;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.IllegalReadFromSmoothDirError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchFileButDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.lang.value.SFile;
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
      execute(SMOOTH_DIR.value());
      fail("exception should be thrown");
    } catch (IllegalReadFromSmoothDirError e) {
      // expected
    }
  }

  @Test
  public void accessToSmoothSubDirIsReported() throws Exception {
    try {
      execute(SMOOTH_DIR.value() + Path.SEPARATOR + "abc");
      fail("exception should be thrown");
    } catch (IllegalReadFromSmoothDirError e) {
      // expected
    }
  }

  @Test
  public void illegalPathIsReported() {
    for (String path : PathTesting.listOfInvalidPaths()) {
      nativeApi = new FakeNativeApi();
      try {
        execute(path);
        fail("exception should be thrown");
      } catch (IllegalPathError e) {
        // expected
      }
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {

    try {
      execute("some/path/file.txt");
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
      execute(dir.value());
      fail("exception should be thrown");
    } catch (NoSuchFileButDirError e) {
      // expected
    }
  }

  @Test
  public void execute() throws Exception {
    given(nativeApi.projectFileSystem()).createFileContainingItsPath(path);
    when(execute(path.value()));
    thenReturned(objectsDb.file(path));
  }

  private SFile execute(String file) {
    return FileFunction.file(nativeApi, nativeApi.string(file));
  }
}
