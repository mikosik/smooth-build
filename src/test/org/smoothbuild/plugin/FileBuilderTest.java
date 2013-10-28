package org.smoothbuild.plugin;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.object.FileObject;
import org.smoothbuild.object.ValueDb;
import org.smoothbuild.plugin.FileBuilder;

public class FileBuilderTest {
  ValueDb valueDb = mock(ValueDb.class);
  FileBuilder fileBuilder = new FileBuilder(valueDb);
  Path path = Path.path("my/path");
  byte[] bytes = new byte[] { 1, 2, 3 };
  FileObject file = Mockito.mock(FileObject.class);

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
  public void opening_output_stream_twice_fails() throws Exception {
    given(fileBuilder).openOutputStream();
    when(fileBuilder).openOutputStream();
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
    given(fileBuilder).openOutputStream();
    when(fileBuilder).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_returns_file_stored_in_object_db_with_empty_content() throws Exception {
    BDDMockito.given(valueDb.file(path, new byte[] {})).willReturn(file);
    given(fileBuilder).openOutputStream();
    given(fileBuilder).setPath(path);
    when(fileBuilder).build();
    thenReturned(file);
  }

  @Test
  public void build_returns_file_stored_in_object_db() throws Exception {
    BDDMockito.given(valueDb.file(path, bytes)).willReturn(file);
    given(fileBuilder.openOutputStream()).write(bytes);
    given(fileBuilder).setPath(path);
    when(fileBuilder).build();
    thenReturned(file);
  }
}
