package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.InputStream;
import java.net.URI;

import org.junit.Test;
import org.smoothbuild.plugin.File;

public class InputClassFileTest {

  @Test(expected = IllegalArgumentException.class)
  public void filesWithoutClassExtensionCausesException() throws Exception {
    inputClassFile("abc");
  }

  @Test
  public void testEquals() {
    assertThat(inputClassFile("abc.class")).isEqualTo(inputClassFile("abc.class"));
    assertThat(inputClassFile("a/b/c.class")).isEqualTo(inputClassFile("a/b/c.class"));

    assertThat(inputClassFile("abc.class")).isNotEqualTo(inputClassFile("a/b/c.class"));
  }

  @Test
  public void aPackage() throws Exception {
    assertThat(inputClassFile("Klass.class").aPackage()).isEqualTo("");
    assertThat(inputClassFile("my/package/Klass.class").aPackage()).isEqualTo("my.package");
    assertThat(inputClassFile("my/package/Klass$Inner.class").aPackage()).isEqualTo("my.package");
  }

  @Test
  public void binaryName() throws Exception {
    assertThat(inputClassFile("Klass.class").binaryName()).isEqualTo("Klass");
    assertThat(inputClassFile("my/package/Klass.class").binaryName()).isEqualTo("my.package.Klass");
    assertThat(inputClassFile("my/package/Klass$Inner.class").binaryName()).isEqualTo(
        "my.package.Klass$Inner");
  }

  @Test
  public void uri() throws Exception {
    InputClassFile inputClassFile = new InputClassFile(path("my-jar.jar"),
        file("my/package/MyKlass.class"));
    assertThat(inputClassFile.toUri()).isEqualTo(
        URI.create("jar:///my-jar.jar:my/package/MyKlass.class"));
  }

  @Test
  public void openInputStream() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    File file = file("my/package/Klass.class");
    when(file.openInputStream()).thenReturn(inputStream);

    assertThat(new InputClassFile(path("my.jar"), file).openInputStream()).isSameAs(inputStream);
  }

  private InputClassFile inputClassFile(String path) {
    return new InputClassFile(path("my.jar"), file(path));
  }

  private File file(String path) {
    File file = mock(File.class);
    when(file.path()).thenReturn(path(path));
    return file;
  }
}
