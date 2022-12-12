package org.smoothbuild.slib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static okio.Okio.sink;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.testing.TestContext;

import okio.BufferedSink;
import okio.ByteString;

public class OutputClassFileTest extends TestContext {
  private final PathS path = PathS.path("my/path");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  public void open_output_stream() throws IOException {
    var nativeApi = nativeApi();
    var factory = nativeApi.factory();
    ArrayBBuilder fileArrayBuilder = bytecodeDb().arrayBuilder(factory.arrayT(factory.fileT()));
    OutputClassFile outputClassFile = new OutputClassFile(fileArrayBuilder, path, nativeApi);
    try (BufferedSink sink = buffer(sink(outputClassFile.openOutputStream()))) {
      sink.write(bytes);
    }
    assertThat(fileArrayBuilder.build().elems(TupleB.class))
        .containsExactly(fileB(path, bytes));
  }

  @Test
  public void get_name_returns_file_path() {
    var arrayTH = arrayTB(fileTB());
    OutputClassFile outputClassFile =
        new OutputClassFile(bytecodeDb().arrayBuilder(arrayTH), path, nativeApi());
    assertThat(outputClassFile.getName())
        .isEqualTo("/" + path);
  }
}
