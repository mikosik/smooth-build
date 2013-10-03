package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.type.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.type.impl.TestFile;

public class OutputClassFileTest {
  TestFile file = new TestFile(path("my/file"));

  OutputClassFile outputClassFile = new OutputClassFile(file);

  @Test
  public void openOutputStream() throws IOException {
    String content = "content";
    StreamTester.writeAndClose(outputClassFile.openOutputStream(), content);
    file.assertContentContains(content);
  }

  @Test
  public void uri() throws Exception {
    String pathString = "my/path/file";
    OutputClassFile inputSourceFile = new OutputClassFile(new TestFile(path(pathString)));
    assertThat(inputSourceFile.getName()).isEqualTo("/" + pathString);
  }
}
