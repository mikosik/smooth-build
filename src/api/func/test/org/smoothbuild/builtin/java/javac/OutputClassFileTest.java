package org.smoothbuild.builtin.java.javac;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static org.hamcrest.Matchers.contains;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.testing.TestingContext;

import okio.BufferedSink;
import okio.ByteString;

public class OutputClassFileTest extends TestingContext {
  private final Path path = Path.path("my/path");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  private ArrayBuilder fileArrayBuilder;
  private OutputClassFile outputClassFile;

  @Test
  public void open_output_stream() throws IOException {
    given(fileArrayBuilder = arrayBuilder((nativeApi().factory()).fileType()));
    given(outputClassFile = new OutputClassFile(fileArrayBuilder, path, nativeApi()));
    try (BufferedSink sink = buffer(sink(outputClassFile.openOutputStream()))) {
      sink.write(bytes);
    }
    when(() -> fileArrayBuilder.build().asIterable(Struct.class));
    thenReturned(contains(file(path, bytes)));
  }

  @Test
  public void get_name_returns_file_path() throws Exception {
    given(outputClassFile = new OutputClassFile(arrayBuilder(fileType()), path, nativeApi()));
    when(outputClassFile.getName());
    thenReturned("/" + path.value());
  }
}
