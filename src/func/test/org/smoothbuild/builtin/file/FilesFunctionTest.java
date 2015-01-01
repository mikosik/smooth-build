package org.smoothbuild.builtin.file;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.rootPath;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.builtin.file.FilesFunction.FilesParameters;
import org.smoothbuild.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.IllegalReadFromSmoothDirError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchDirButFileError;
import org.smoothbuild.io.fs.base.err.NoSuchDirError;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.PathTesting;
import org.smoothbuild.testing.task.exec.FakeNativeApi;
import org.testory.Closure;

public class FilesFunctionTest {
  private FakeObjectsDb objectsDb;
  private FakeNativeApi nativeApi = new FakeNativeApi();
  private Path path;
  private Path path1;
  private Path path2;
  private Path dir;

  @Test
  public void listing_files_from_project_root_dir_is_forbidden() throws Exception {
    when($files(nativeApi, params(rootPath().value())));
    thenThrown(CannotListRootDirError.class);
  }

  @Test
  public void listing_files_from_smooth_dir_is_forbidden() throws Exception {
    when($files(nativeApi, params(SMOOTH_DIR.value())));
    thenThrown(IllegalReadFromSmoothDirError.class);
  }

  @Test
  public void listing_files_from_subdir_in_smooth_dir_is_forbidden() throws Exception {
    when($files(nativeApi, params(SMOOTH_DIR.value() + Path.SEPARATOR + "abc")));
    thenThrown(IllegalReadFromSmoothDirError.class);
  }

  @Test
  public void illegal_path_is_forbidden() {
    for (String path : PathTesting.listOfInvalidPaths()) {
      when($files(nativeApi, params(path)));
      thenThrown(IllegalPathError.class);
    }
  }

  @Test
  public void nonexistent_path_is_forbidden() throws Exception {
    when($files(nativeApi, params("some/path")));
    thenThrown(NoSuchDirError.class);
  }

  @Test
  public void non_dir_path_is_forbidden() throws Exception {
    given(path = path("some/path/file.txt"));
    given(nativeApi.projectFileSystem()).createFile(path, "");
    when($files(nativeApi, params(path.value())));
    thenThrown(NoSuchDirButFileError.class);
  }

  @Test
  public void files_returns_files_read_from_project_filesystem() throws Throwable {
    given(dir = path("root/path"));
    given(path1 = path("file/file.txt"));
    given(path2 = path("file/file2.txt"));
    given(nativeApi.projectFileSystem()).createFile(dir.append(path1), "file1");
    given(nativeApi.projectFileSystem()).createFile(dir.append(path2), "file2");
    given(objectsDb = new FakeObjectsDb());

    when($files(nativeApi, params(dir.value())));

    thenReturned(containsInAnyOrder(objectsDb.file(path1, "file1"), objectsDb.file(path2, "file2")));
  }

  private static FilesParameters params(final String dir) {
    return new FilesParameters() {
      @Override
      public SString dir() {
        return new FakeObjectsDb().string(dir);
      }
    };
  }

  private static Closure $files(final NativeApiImpl nativeApi, final FilesParameters params) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return FilesFunction.files(nativeApi, params);
      }
    };
  }
}
