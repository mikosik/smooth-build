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
import org.smoothbuild.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.IllegalReadFromSmoothDirError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchDirButFileError;
import org.smoothbuild.io.fs.base.err.NoSuchDirError;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.task.exec.ContainerImpl;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.PathTesting;
import org.smoothbuild.testing.task.exec.FakeContainer;
import org.testory.Closure;

public class FilesFunctionTest {
  private FakeObjectsDb objectsDb;
  private final FakeContainer container = new FakeContainer();
  private Path path;
  private Path path1;
  private Path path2;
  private Path dir;

  @Test
  public void listing_files_from_project_root_dir_is_forbidden() throws Exception {
    when($files(container, params(rootPath().value())));
    thenThrown(CannotListRootDirError.class);
  }

  @Test
  public void listing_files_from_smooth_dir_is_forbidden() throws Exception {
    when($files(container, params(SMOOTH_DIR.value())));
    thenThrown(IllegalReadFromSmoothDirError.class);
  }

  @Test
  public void listing_files_from_subdir_in_smooth_dir_is_forbidden() throws Exception {
    when($files(container, params(SMOOTH_DIR.value() + Path.SEPARATOR + "abc")));
    thenThrown(IllegalReadFromSmoothDirError.class);
  }

  @Test
  public void illegal_path_is_forbidden() {
    for (String path : PathTesting.listOfInvalidPaths()) {
      when($files(container, params(path)));
      thenThrown(IllegalPathError.class);
    }
  }

  @Test
  public void nonexistent_path_is_forbidden() throws Exception {
    when($files(container, params("some/path")));
    thenThrown(NoSuchDirError.class);
  }

  @Test
  public void non_dir_path_is_forbidden() throws Exception {
    given(path = path("some/path/file.txt"));
    given(container.projectFileSystem()).createFile(path, "");
    when($files(container, params(path.value())));
    thenThrown(NoSuchDirButFileError.class);
  }

  @Test
  public void files_returns_files_read_from_project_filesystem() throws Throwable {
    given(dir = path("root/path"));
    given(path1 = path("file/file.txt"));
    given(path2 = path("file/file2.txt"));
    given(container.projectFileSystem()).createFile(dir.append(path1), "file1");
    given(container.projectFileSystem()).createFile(dir.append(path2), "file2");
    given(objectsDb = new FakeObjectsDb());

    when($files(container, params(dir.value())));

    thenReturned(containsInAnyOrder(objectsDb.file(path1, "file1"), objectsDb.file(path2, "file2")));
  }

  private static SString params(final String dir) {
    return new FakeObjectsDb().string(dir);
  }

  private static Closure $files(final ContainerImpl container, final SString dir) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return FilesFunction.files(container, dir);
      }
    };
  }
}
