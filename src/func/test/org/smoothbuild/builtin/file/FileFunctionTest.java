package org.smoothbuild.builtin.file;

import static org.junit.Assert.fail;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.db.objects.ObjectsDb.objectsDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.smoothbuild.testing.db.objects.ValueCreators.file;
import static org.smoothbuild.testing.io.fs.base.FileSystems.createFile;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.IllegalReadFromSmoothDirError;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchFileButDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.task.exec.ContainerImpl;
import org.smoothbuild.testing.io.fs.base.PathTesting;

public class FileFunctionTest {
  private final ObjectsDb objectsDb = objectsDb();
  private ContainerImpl container = containerImpl();
  private final Path path = path("file/path/file.txt");

  @Test
  public void access_to_smooth_dir_is_reported() throws Exception {
    try {
      execute(SMOOTH_DIR.value());
      fail("exception should be thrown");
    } catch (IllegalReadFromSmoothDirError e) {
      // expected
    }
  }

  @Test
  public void access_to_subdir_in_smooth_dir_is_reported() throws Exception {
    try {
      execute(SMOOTH_DIR.value() + Path.SEPARATOR + "abc");
      fail("exception should be thrown");
    } catch (IllegalReadFromSmoothDirError e) {
      // expected
    }
  }

  @Test
  public void illegal_path_is_reported() {
    for (String path : PathTesting.listOfInvalidPaths()) {
      container = containerImpl();
      try {
        execute(path);
        fail("exception should be thrown");
      } catch (IllegalPathError e) {
        // expected
      }
    }
  }

  @Test
  public void nonexistent_path_is_reported() throws Exception {

    try {
      execute("some/path/file.txt");
      fail("exception should be thrown");
    } catch (NoSuchFileError e) {
      // expected
    }
  }

  @Test
  public void dir_path_is_reported() throws Exception {
    Path dir = path("some/path");
    Path file = dir.append(path("file.txt"));
    createFile(container.projectFileSystem(), file, "content");

    try {
      execute(dir.value());
      fail("exception should be thrown");
    } catch (NoSuchFileButDirError e) {
      // expected
    }
  }

  @Test
  public void execute() throws Exception {
    given(createFile(container.projectFileSystem(), path, "content"));
    when(execute(path.value()));
    thenReturned(file(objectsDb, path, "content"));
  }

  private SFile execute(String file) {
    return FileFunction.file(container, container.create().string(file));
  }
}
