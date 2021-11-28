package org.smoothbuild.slib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static okio.Okio.sink;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.TestingContext;

import okio.BufferedSink;
import okio.ByteString;

public class OutputClassFileTest extends TestingContext {
  private final Path path = Path.path("my/path");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  public void open_output_stream() throws IOException {
    ArrayHBuilder fileArrayBuilder = objectHDb().arrayBuilder(nativeApi().factory().fileT());
    OutputClassFile outputClassFile = new OutputClassFile(fileArrayBuilder, path, nativeApi());
    try (BufferedSink sink = buffer(sink(outputClassFile.openOutputStream()))) {
      sink.write(bytes);
    }
    assertThat(fileArrayBuilder.build().elements(TupleH.class))
        .containsExactly(fileH(path, bytes));
  }

  @Test
  public void get_name_returns_file_path() {
    OutputClassFile outputClassFile =
        new OutputClassFile(objectHDb().arrayBuilder(fileHT()), path, nativeApi());
    assertThat(outputClassFile.getName())
        .isEqualTo("/" + path);
  }
}
