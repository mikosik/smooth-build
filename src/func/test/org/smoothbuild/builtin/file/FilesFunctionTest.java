package org.smoothbuild.builtin.file;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.rootPath;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.smoothbuild.testing.io.fs.base.FileSystems.createFile;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.IllegalReadFromSmoothDirError;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchDirButFileException;
import org.smoothbuild.io.fs.base.err.NoSuchDirException;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.task.exec.ContainerImpl;
import org.smoothbuild.testing.io.fs.base.PathTesting;
import org.testory.Closure;

public class FilesFunctionTest {
  private ValuesDb valuesDb;
  private final ContainerImpl container = containerImpl();
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
    thenThrown(NoSuchDirException.class);
  }

  @Test
  public void non_dir_path_is_forbidden() throws Exception {
    given(path = path("some/path/file.txt"));
    given(createFile(container.projectFileSystem(), path, ""));
    when($files(container, params(path.value())));
    thenThrown(NoSuchDirButFileException.class);
  }

  @Test
  public void files_returns_files_read_from_project_filesystem() throws Throwable {
    given(dir = path("root/path"));
    given(path1 = path("file/file.txt"));
    given(path2 = path("file/file2.txt"));
    given(createFile(container.projectFileSystem(), dir.append(path1), "file1"));
    given(createFile(container.projectFileSystem(), dir.append(path2), "file2"));
    given(valuesDb = valuesDb());

    when($files(container, params(dir.value())));

    thenReturned(containsInAnyOrder(file(valuesDb, path1, "file1"), file(valuesDb, path2,
        "file2")));
  }

  private static SString params(final String dir) {
    return valuesDb().string(dir);
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
