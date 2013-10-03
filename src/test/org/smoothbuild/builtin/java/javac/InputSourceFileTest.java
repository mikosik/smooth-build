package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.type.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.type.api.Path;

public class InputSourceFileTest {

  @Test
  public void getCharContent() throws IOException {
    Path path = path("my/path");
    TestFile file = new TestFile(path);
    file.createContentWithFilePath();

    CharSequence actual = new InputSourceFile(file).getCharContent(true);

    assertThat(actual).isEqualTo(path.value());
  }

  @Test
  public void uri() throws Exception {
    String pathString = "my/path/file";
    InputSourceFile inputSourceFile = new InputSourceFile(new TestFile(path(pathString)));
    assertThat(inputSourceFile.getName()).isEqualTo("/" + pathString);
  }
}
