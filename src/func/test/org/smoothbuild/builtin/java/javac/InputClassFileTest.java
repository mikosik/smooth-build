package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.net.URI;

import org.junit.Test;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class InputClassFileTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();

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
    InputClassFile inputClassFile = inputClassFile("my/package/MyKlass.class");
    assertThat(inputClassFile.toUri()).isEqualTo(URI.create("jar:///:my/package/MyKlass.class"));
  }

  @Test
  public void openInputStream() throws Exception {
    String content = "some content";
    SFile file = objectsDb.file(path("my/package/Klass.class"), content);
    InputClassFile inputClassFile = new InputClassFile(file);

    assertThat(inputStreamToString(inputClassFile.openInputStream())).isEqualTo(content);
  }

  private InputClassFile inputClassFile(String path) {
    return new InputClassFile(objectsDb.file(path(path)));
  }
}
