package org.smoothbuild.stdlib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static okio.Okio.sink;

import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class OutputClassFileTest extends TestingVirtualMachine {
  private final Path path = Path.path("my/path");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  public void open_output_stream() throws Exception {
    var nativeApi = nativeApi();
    var factory = nativeApi.factory();
    ArrayBBuilder fileArrayBuilder = exprDb().arrayBuilder(factory.arrayT(factory.fileT()));
    OutputClassFile outputClassFile = new OutputClassFile(fileArrayBuilder, path, nativeApi);
    try (BufferedSink sink = buffer(sink(outputClassFile.openOutputStream()))) {
      sink.write(bytes);
    }
    assertThat(fileArrayBuilder.build().elements(TupleB.class)).containsExactly(fileB(path, bytes));
  }

  @Test
  public void get_name_returns_file_path() throws Exception {
    var arrayTH = arrayTB(fileTB());
    OutputClassFile outputClassFile =
        new OutputClassFile(exprDb().arrayBuilder(arrayTH), path, nativeApi());
    assertThat(outputClassFile.getName()).isEqualTo("/" + path);
  }
}
