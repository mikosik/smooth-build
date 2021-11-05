package org.smoothbuild.slib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class InputSourceFileTest extends TestingContext {
  @Test
  public void get_char_content_returns_file_content() {
    Path path = path("my/path");
    Tuple file = file(path, ByteString.encodeUtf8("abc"));
    assertThat(new InputSourceFile(file).getCharContent(true).toString())
        .isEqualTo("abc");
  }

  @Test
  public void uri() {
    Path path = path("my/path");
    Tuple file = file(path);
    assertThat(new InputSourceFile(file).getName())
        .isEqualTo("/" + path.toString());
  }
}
