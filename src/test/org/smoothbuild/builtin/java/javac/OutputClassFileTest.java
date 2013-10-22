package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.object.FileBuilder;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

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
    FileSet fileSet = fileSetBuilder.build();

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
