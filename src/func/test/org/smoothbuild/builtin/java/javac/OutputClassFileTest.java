package org.smoothbuild.builtin.java.javac;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class OutputClassFileTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final Path path = Path.path("my/path");
  private final String content = "content";

  private ArrayBuilder<SFile> fileArrayBuilder;
  private OutputClassFile outputClassFile;

  @Test
  public void openOutputStream() throws IOException {
    given(fileArrayBuilder = objectsDb.arrayBuilder(SFile.class));
    given(outputClassFile = new OutputClassFile(fileArrayBuilder, path, objectsDb));
    StreamTester.writeAndClose(outputClassFile.openOutputStream(), content);
    when(fileArrayBuilder).build();
    thenReturned(Matchers.contains(objectsDb.file(path, content)));
  }

  @Test
  public void get_name_returns_file_path() throws Exception {
    given(outputClassFile =
        new OutputClassFile(objectsDb.arrayBuilder(SFile.class), path, objectsDb));
    when(outputClassFile.getName());
    thenReturned("/" + path.value());
  }
}
