package org.smoothbuild.builtin.java.javac;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static org.hamcrest.Matchers.contains;
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
import org.smoothbuild.task.exec.TestingContainer;

import okio.BufferedSink;
import okio.ByteString;

public class OutputClassFileTest {
  private final NativeApi nativeApi = new TestingContainer();
  private final Path path = Path.path("my/path");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  private ArrayBuilder fileArrayBuilder;
  private OutputClassFile outputClassFile;

  @Test
  public void open_output_stream() throws IOException {
    given(fileArrayBuilder = nativeApi.create().arrayBuilder(nativeApi.types().file()));
    given(outputClassFile = new OutputClassFile(fileArrayBuilder, path, nativeApi));
    try (BufferedSink sink = buffer(sink(outputClassFile.openOutputStream()))) {
      sink.write(bytes);
    }
    when(() -> fileArrayBuilder.build().asIterable(Struct.class));
    thenReturned(contains(file(nativeApi.create(), path, bytes)));
  }

  @Test
  public void get_name_returns_file_path() throws Exception {
    given(outputClassFile = new OutputClassFile(nativeApi.create()
        .arrayBuilder(nativeApi.types().file()), path, nativeApi));
    when(outputClassFile.getName());
    thenReturned("/" + path.value());
  }
}
