package org.smoothbuild.builtin.java;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.db.objects.ObjectsDb.objectsDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.smoothbuild.testing.db.objects.ValueCreators.file;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.builtin.util.Predicates;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.testing.common.JarTester;

public class UnjarerTest {
  private final Path path1 = path("file/path/file1.txt");
  private final Path path2 = path("file/path/file2.txt");

  private final Container container = containerImpl();
  private final Unjarer unjarer = new Unjarer(container);
  private Blob blob;
  private SFile file1;
  private SFile file2;

  @Test
  public void unjars_two_files() throws Exception {
    given(file1 = file(objectsDb(), path1));
    given(file2 = file(objectsDb(), path2));
    given(blob = JarTester.jar(file1, file2));
    when(unjarer.unjar(blob));
    thenReturned(contains(file1, file2));
  }

  @Test
  public void unjars_files_that_match_filter() throws Exception {
    given(file1 = file(objectsDb(), path1));
    given(file2 = file(objectsDb(), path2));
    given(blob = JarTester.jar(file1, file2));
    when(unjarer.unjar(blob, Predicates.equalTo(path2.value())));
    thenReturned(contains(file2));
  }
}
