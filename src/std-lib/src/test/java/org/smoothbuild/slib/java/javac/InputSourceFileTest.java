package org.smoothbuild.slib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.fs.base.PathS.path;

import org.junit.jupiter.api.Test;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

import okio.ByteString;

public class InputSourceFileTest extends TestContext {
  @Test
  public void get_char_content_returns_file_content() {
    PathS path = path("my/path");
    TupleB file = fileB(path, ByteString.encodeUtf8("abc"));
    assertThat(new InputSourceFile(file).getCharContent(true).toString())
        .isEqualTo("abc");
  }

  @Test
  public void uri() {
    PathS path = path("my/path");
    TupleB file = fileB(path);
    assertThat(new InputSourceFile(file).getName())
        .isEqualTo("/" + path.toString());
  }
}
