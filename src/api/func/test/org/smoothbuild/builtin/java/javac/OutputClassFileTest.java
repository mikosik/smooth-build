package org.smoothbuild.builtin.java.javac;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.task.exec.Container.container;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.util.Streams;

public class OutputClassFileTest {
  private final NativeApi nativeApi = container();
  private final Path path = Path.path("my/path");
  private final byte[] bytes = new byte[] { 1, 2, 3 };

  private ArrayBuilder fileArrayBuilder;
  private OutputClassFile outputClassFile;

  @Test
  public void open_output_stream() throws IOException {
    given(fileArrayBuilder = nativeApi.create().arrayBuilder(FILE));
    given(outputClassFile = new OutputClassFile(fileArrayBuilder, path, nativeApi));
    Streams.writeAndClose(outputClassFile.openOutputStream(), bytes);
    when(() -> fileArrayBuilder.build().asIterable(Struct.class));
    thenReturned(contains(file(memoryValuesDb(), path, bytes)));
  }

  @Test
  public void get_name_returns_file_path() throws Exception {
    given(outputClassFile = new OutputClassFile(nativeApi.create().arrayBuilder(FILE), path,
        nativeApi));
    when(outputClassFile.getName());
    thenReturned("/" + path.value());
  }
}
