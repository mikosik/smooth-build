package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.ContentFunction.content;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.task.exec.ContainerImpl;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.exec.FakeContainer;

public class ContentFunctionTest {
  private FakeObjectsDb objectsDb;
  private ContainerImpl container;
  private SFile file;

  @Before
  public void before() {
    givenTest(this);
    given(objectsDb = new FakeObjectsDb());
    given(container = new FakeContainer());
  }

  @Test
  public void content_of_file_is_returned_as_blob() {
    given(file = objectsDb.file(path("some/path"), objectsDb.blob("content")));
    when(content(container, file));
    thenReturned(file.content());
  }
}
