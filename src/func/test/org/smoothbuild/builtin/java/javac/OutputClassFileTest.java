package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.builtin.java.javac.OutputClassFile;
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
    given(fileArrayBuilder = objectsDb.arrayBuilder(FILE_ARRAY));
    given(outputClassFile = new OutputClassFile(fileArrayBuilder, path, objectsDb));
    StreamTester.writeAndClose(outputClassFile.openOutputStream(), content);
    when(fileArrayBuilder).build();
    thenReturned(Matchers.contains(objectsDb.file(path, content)));
  }

  @Test
  public void uri() throws Exception {
    Path path = Path.path("my/path");
    ArrayBuilder<SFile> fileArrayBuilder = objectsDb.arrayBuilder(FILE_ARRAY);

    OutputClassFile outputClassFile = new OutputClassFile(fileArrayBuilder, path, objectsDb);

    assertThat(outputClassFile.getName()).isEqualTo("/" + path.value());
  }
}
