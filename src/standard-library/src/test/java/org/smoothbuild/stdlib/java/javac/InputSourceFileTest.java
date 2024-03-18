package org.smoothbuild.stdlib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.Path.path;

import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class InputSourceFileTest extends TestingVirtualMachine {
  @Test
  public void get_char_content_returns_file_content() throws Exception {
    Path path = path("my/path");
    TupleB file = fileB(path, ByteString.encodeUtf8("abc"));
    assertThat(new InputSourceFile(file).getCharContent(true).toString()).isEqualTo("abc");
  }

  @Test
  public void uri() throws Exception {
    Path path = path("my/path");
    TupleB file = fileB(path);
    assertThat(new InputSourceFile(file).getName()).isEqualTo("/" + path.toString());
  }
}
