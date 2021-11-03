package org.smoothbuild.slib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static okio.Okio.sink;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.TestingContextImpl;

import okio.BufferedSink;
import okio.ByteString;

public class OutputClassFileTest extends TestingContextImpl {
  private final Path path = Path.path("my/path");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  public void open_output_stream() throws IOException {
    ArrayBuilder fileArrayBuilder = objectDb().arrayBuilder(nativeApi().factory().fileType());
    OutputClassFile outputClassFile = new OutputClassFile(fileArrayBuilder, path, nativeApi());
    try (BufferedSink sink = buffer(sink(outputClassFile.openOutputStream()))) {
      sink.write(bytes);
    }
    assertThat(fileArrayBuilder.build().elements(Struc_.class))
        .containsExactly(file(path, bytes));
  }

  @Test
  public void get_name_returns_file_path() {
    OutputClassFile outputClassFile =
        new OutputClassFile(objectDb().arrayBuilder(fileSpec()), path, nativeApi());
    assertThat(outputClassFile.getName())
        .isEqualTo("/" + path);
  }
}
