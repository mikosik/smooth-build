package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class InputSourceFileTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private Path path;
  private String content;
  private SFile file;

  @Test
  public void get_char_content_returns_file_content() throws IOException {
    given(path = path("my/path"));
    given(content = "some content");
    given(file = objectsDb.file(path, content));
    when(new InputSourceFile(file)).getCharContent(true);
    thenReturned(content);
  }

  @Test
  public void uri() throws Exception {
    given(path = path("my/path"));
    given(file = objectsDb.file(path));
    when(new InputSourceFile(file)).getName();
    thenReturned("/" + path.value());
  }
}
