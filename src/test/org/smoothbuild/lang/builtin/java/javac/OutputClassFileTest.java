package org.smoothbuild.lang.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.lang.type.FileTester;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.Iterables;

public class OutputClassFileTest {
  FakeSandbox sandbox = new FakeSandbox();

  @Test
  public void openOutputStream() throws IOException {
    Path path = Path.path("my/path");
    ArrayBuilder<SFile> fileArrayBuilder = sandbox.arrayBuilder(FILE_ARRAY);
    FileBuilder fileBuilder = sandbox.fileBuilder();

    OutputClassFile outputClassFile = new OutputClassFile(fileArrayBuilder, path, fileBuilder);

    String content = "content";
    StreamTester.writeAndClose(outputClassFile.openOutputStream(), content);
    SArray<SFile> fileArray = fileArrayBuilder.build();

    assertThat(Iterables.size(fileArray)).isEqualTo(1);
    SFile file = fileArray.iterator().next();
    assertThat(file.path()).isEqualTo(path);
    FileTester.assertContentContains(file, content);
  }

  @Test
  public void uri() throws Exception {
    Path path = Path.path("my/path");
    ArrayBuilder<SFile> fileArrayBuilder = sandbox.arrayBuilder(FILE_ARRAY);
    FileBuilder fileBuilder = sandbox.fileBuilder();

    OutputClassFile outputClassFile = new OutputClassFile(fileArrayBuilder, path, fileBuilder);

    assertThat(outputClassFile.getName()).isEqualTo("/" + path.value());
  }
}
