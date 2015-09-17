package org.smoothbuild.builtin.java.javac;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.task.exec.FakeContainer;

public class OutputClassFileTest {
  private final FakeContainer container = new FakeContainer();
  private final Path path = Path.path("my/path");
  private final String content = "content";

  private ArrayBuilder<SFile> fileArrayBuilder;
  private OutputClassFile outputClassFile;

  @Test
  public void openOutputStream() throws IOException {
    given(fileArrayBuilder = container.arrayBuilder(SFile.class));
    given(outputClassFile = new OutputClassFile(fileArrayBuilder, path, container));
    StreamTester.writeAndClose(outputClassFile.openOutputStream(), content);
    when(fileArrayBuilder).build();
    thenReturned(Matchers.contains(container.file(path, content)));
  }

  @Test
  public void get_name_returns_file_path() throws Exception {
    given(outputClassFile =
        new OutputClassFile(container.arrayBuilder(SFile.class), path, container));
    when(outputClassFile.getName());
    thenReturned("/" + path.value());
  }
}
