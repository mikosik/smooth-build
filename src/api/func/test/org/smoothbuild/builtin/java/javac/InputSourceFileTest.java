package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Struct;

public class InputSourceFileTest {
  private final ValuesDb valuesDb = memoryValuesDb();
  private Path path;
  private String content;
  private Struct file;

  @Test
  public void get_char_content_returns_file_content() throws IOException {
    given(path = path("my/path"));
    given(content = "some content");
    given(file = file(valuesDb, path, content.getBytes(CHARSET)));
    when(new InputSourceFile(file)).getCharContent(true);
    thenReturned(content);
  }

  @Test
  public void uri() throws Exception {
    given(path = path("my/path"));
    given(file = file(valuesDb, path));
    when(new InputSourceFile(file)).getName();
    thenReturned("/" + path.value());
  }
}
