package org.smoothbuild.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.exec.FakeContainer;

public class PathFunctionTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final FakeContainer container = new FakeContainer();
  private Path path;
  private SFile file;

  @Test
  public void file_path_is_returned_as_string() throws Exception {
    given(path = path("some/path"));
    given(file = objectsDb.file(path, ""));
    when(PathFunction.path(container, file));
    thenReturned(objectsDb.string(path.value()));
  }
}
