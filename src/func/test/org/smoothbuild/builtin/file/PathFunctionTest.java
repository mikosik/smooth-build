package org.smoothbuild.builtin.file;

import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.SFile;

public class PathFunctionTest {
  private final ValuesDb valuesDb = valuesDb();
  private final Container container = containerImpl();
  private Path path;
  private SFile file;

  @Test
  public void file_path_is_returned_as_string() throws Exception {
    given(path = path("some/path"));
    given(file = file(valuesDb, path, ""));
    when(PathFunction.path(container, file));
    thenReturned(valuesDb.string(path.value()));
  }
}
