package org.smoothbuild.db.objects.build;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.db.objects.instance.FileObject;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.testing.lang.type.FakeBlob;

public class FileWriterTest {
  ObjectsDb objectsDb = mock(ObjectsDb.class);
  FileWriter fileWriter = new FileWriter(objectsDb);
  Path path = Path.path("my/path");
  SBlob blob = new FakeBlob();
  byte[] bytes = new byte[] { 1, 2, 3 };
  FileObject file = mock(FileObject.class);

  @Test
  public void setting_null_path_fails() throws Exception {
    when(fileWriter).setPath(null);
    thenThrown(NullPointerException.class);
  }

  @Test
  public void setting_path_twice_fails() throws Exception {
    given(fileWriter).setPath(path);
    when(fileWriter).setPath(path);
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void setting_null_content_fails() throws Exception {
    when(fileWriter).setContent(null);
    thenThrown(NullPointerException.class);
  }

  @Test
  public void setting_content_twice_fails() throws Exception {
    given(fileWriter).setContent(blob);
    when(fileWriter).setContent(blob);
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_fails_when_no_content_was_provided() {
    given(fileWriter).setPath(path);
    when(fileWriter).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_fails_when_no_path_was_provided() {
    given(fileWriter).setContent(blob);
    when(fileWriter).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_returns_file_stored_in_object_db() throws Exception {
    given(blob = new FakeBlob(bytes));
    given(willReturn(file), objectsDb).writeFile(path, blob);
    given(fileWriter).setContent(blob);
    given(fileWriter).setPath(path);
    when(fileWriter).build();
    thenReturned(file);
  }
}
