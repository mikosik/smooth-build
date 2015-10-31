package org.smoothbuild.builtin.java.javac;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.testing.common.StreamTester;

public class OutputClassFileTest {
  private final Container container = containerImpl();
  private final Path path = Path.path("my/path");
  private final String content = "content";

  private ArrayBuilder<SFile> fileArrayBuilder;
  private OutputClassFile outputClassFile;

  @Test
  public void open_output_stream() throws IOException {
    given(fileArrayBuilder = container.create().arrayBuilder(SFile.class));
    given(outputClassFile = new OutputClassFile(fileArrayBuilder, path, container));
    StreamTester.writeAndClose(outputClassFile.openOutputStream(), content);
    when(fileArrayBuilder).build();
    thenReturned(contains(file(valuesDb(), path, content)));
  }

  @Test
  public void get_name_returns_file_path() throws Exception {
    given(outputClassFile = new OutputClassFile(container.create().arrayBuilder(SFile.class), path,
        container));
    when(outputClassFile.getName());
    thenReturned("/" + path.value());
  }
}
