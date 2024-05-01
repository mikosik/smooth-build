package org.smoothbuild.stdlib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static okio.Okio.sink;

import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.testing.TestingVm;

public class OutputClassFileTest extends TestingVm {
  private final Path path = Path.path("my/path");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  void open_output_stream() throws Exception {
    var nativeApi = nativeApi();
    var factory = nativeApi.factory();
    var fileArrayBuilder = exprDb().newArrayBuilder(factory.arrayType(factory.fileType()));
    var outputClassFile = new OutputClassFile(fileArrayBuilder, path, nativeApi);
    try (BufferedSink sink = buffer(sink(outputClassFile.openOutputStream()))) {
      sink.write(bytes);
    }
    assertThat(fileArrayBuilder.build().elements(BTuple.class)).containsExactly(bFile(path, bytes));
  }

  @Test
  void get_name_returns_file_path() throws Exception {
    var arrayTH = bArrayType(bFileType());
    var outputClassFile = new OutputClassFile(exprDb().newArrayBuilder(arrayTH), path, nativeApi());
    assertThat(outputClassFile.getName()).isEqualTo("/" + path);
  }
}
