package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.builtin.java.javac.InputSourceFile;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class InputSourceFileTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();

  @Test
  public void getCharContent() throws IOException {
    Path path = path("my/path");
    SFile file = objectsDb.file(path);

    CharSequence actual = new InputSourceFile(file).getCharContent(true);

    assertThat(actual).isEqualTo(path.value());
  }

  @Test
  public void uri() throws Exception {
    String pathString = "my/path/file";
    SFile file = objectsDb.file(path(pathString));
    InputSourceFile inputSourceFile = new InputSourceFile(file);
    assertThat(inputSourceFile.getName()).isEqualTo("/" + pathString);
  }
}
