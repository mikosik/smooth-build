package org.smoothbuild.io.cache.value.build;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.io.cache.value.instance.CachedFile;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.testing.lang.type.FakeBlob;

public class FileBuilderTest {
  ValueDb valueDb = mock(ValueDb.class);
  FileBuilder fileBuilder = new FileBuilder(valueDb);
  Path path = Path.path("my/path");
  SBlob blob = new FakeBlob();
  byte[] bytes = new byte[] { 1, 2, 3 };
  CachedFile file = mock(CachedFile.class);

  @Test
  public void setting_null_path_fails() throws Exception {
    when(fileBuilder).setPath(null);
    thenThrown(NullPointerException.class);
  }

  @Test
  public void setting_path_twice_fails() throws Exception {
    given(fileBuilder).setPath(path);
    when(fileBuilder).setPath(path);
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void setting_null_content_fails() throws Exception {
    when(fileBuilder).setContent(null);
    thenThrown(NullPointerException.class);
  }

  @Test
  public void setting_content_twice_fails() throws Exception {
    given(fileBuilder).setContent(blob);
    when(fileBuilder).setContent(blob);
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_fails_when_no_content_was_provided() {
    given(fileBuilder).setPath(path);
    when(fileBuilder).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_fails_when_no_path_was_provided() {
    given(fileBuilder).setContent(blob);
    when(fileBuilder).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_returns_file_stored_in_object_db() throws Exception {
    given(blob = new FakeBlob(bytes));
    given(willReturn(file), valueDb).writeFile(path, blob);
    given(fileBuilder).setContent(blob);
    given(fileBuilder).setPath(path);
    when(fileBuilder).build();
    thenReturned(file);
  }
}
