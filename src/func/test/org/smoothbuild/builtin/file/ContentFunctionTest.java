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
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

public class ContentFunctionTest {
  private FakeObjectsDb objectsDb;
  private NativeApiImpl nativeApi;
  private SFile file;

  @Before
  public void before() {
    givenTest(this);
    given(objectsDb = new FakeObjectsDb());
    given(nativeApi = new FakeNativeApi());
  }

  @Test
  public void content_of_file_is_returned_as_blob() {
    given(file = objectsDb.file(path("some/path"), objectsDb.blob("content")));
    when(content(nativeApi, file));
    thenReturned(file.content());
  }
}
