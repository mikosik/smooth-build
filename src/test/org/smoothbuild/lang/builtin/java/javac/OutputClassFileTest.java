package org.smoothbuild.lang.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.FileSetBuilder;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.lang.type.FileTester;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.Iterables;

public class OutputClassFileTest {
  FakeSandbox sandbox = new FakeSandbox();

  @Test
  public void openOutputStream() throws IOException {
    Path path = Path.path("my/path");
    FileSetBuilder fileSetBuilder = sandbox.fileSetBuilder();
    FileBuilder fileBuilder = sandbox.fileBuilder();

    OutputClassFile outputClassFile = new OutputClassFile(fileSetBuilder, path, fileBuilder);

    String content = "content";
    StreamTester.writeAndClose(outputClassFile.openOutputStream(), content);
    Array<File> fileSet = fileSetBuilder.build();

    assertThat(Iterables.size(fileSet)).isEqualTo(1);
    File file = fileSet.iterator().next();
    assertThat(file.path()).isEqualTo(path);
    FileTester.assertContentContains(file, content);
  }

  @Test
  public void uri() throws Exception {
    Path path = Path.path("my/path");
    FileSetBuilder fileSetBuilder = sandbox.fileSetBuilder();
    FileBuilder fileBuilder = sandbox.fileBuilder();

    OutputClassFile outputClassFile = new OutputClassFile(fileSetBuilder, path, fileBuilder);

    assertThat(outputClassFile.getName()).isEqualTo("/" + path.value());
  }
}
