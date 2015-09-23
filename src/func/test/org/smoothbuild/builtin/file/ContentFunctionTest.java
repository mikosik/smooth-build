package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.ContentFunction.content;
import static org.smoothbuild.db.objects.ObjectsDb.objectsDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.smoothbuild.testing.db.objects.ValueCreators.blob;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.task.exec.ContainerImpl;

public class ContentFunctionTest {
  private ObjectsDb objectsDb;
  private ContainerImpl container;
  private SFile file;

  @Before
  public void before() {
    givenTest(this);
    given(objectsDb = objectsDb());
    given(container = containerImpl());
  }

  @Test
  public void content_of_file_is_returned_as_blob() {
    given(file = objectsDb.file(path("some/path"), blob(objectsDb, "content")));
    when(content(container, file));
    thenReturned(file.content());
  }
}
