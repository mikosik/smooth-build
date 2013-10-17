package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.type.impl.TestFile;

public class OutputClassFileTest {
  Path path = path("my/path/file");
  TestFile file = new TestFile(path);

  OutputClassFile outputClassFile = new OutputClassFile(path, file.openOutputStream());

  @Test
  public void openOutputStream() throws IOException {
    String content = "content";
    StreamTester.writeAndClose(outputClassFile.openOutputStream(), content);
    file.assertContentContains(content);
  }

  @Test
  public void uri() throws Exception {
    OutputClassFile inputSourceFile = new OutputClassFile(path,
        new TestFile(path).openOutputStream());
    assertThat(inputSourceFile.getName()).isEqualTo("/" + path.value());
  }
}
