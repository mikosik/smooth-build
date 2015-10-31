package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.ContentFunction.content;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.smoothbuild.testing.db.values.ValueCreators.blob;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.task.exec.ContainerImpl;

public class ContentFunctionTest {
  private ValuesDb valuesDb;
  private ContainerImpl container;
  private SFile file;

  @Before
  public void before() {
    givenTest(this);
    given(valuesDb = valuesDb());
    given(container = containerImpl());
  }

  @Test
  public void content_of_file_is_returned_as_blob() {
    given(file = valuesDb.file(path("some/path"), blob(valuesDb, "content")));
    when(content(container, file));
    thenReturned(file.content());
  }
}
