package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Struct;

import okio.ByteString;

public class InputSourceFileTest {
  private Path path;
  private Struct file;

  @Test
  public void get_char_content_returns_file_content() throws IOException {
    given(path = path("my/path"));
    given(file = file(path, ByteString.encodeUtf8("abc")));
    when(new InputSourceFile(file)).getCharContent(true);
    thenReturned("abc");
  }

  @Test
  public void uri() throws Exception {
    given(path = path("my/path"));
    given(file = file(path));
    when(new InputSourceFile(file)).getName();
    thenReturned("/" + path.value());
  }
}
