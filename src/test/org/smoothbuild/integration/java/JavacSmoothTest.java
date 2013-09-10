package org.smoothbuild.integration.java;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;

public class JavacSmoothTest extends IntegrationTestCase {

  @Test
  public void compileOneFile() throws Exception {
    String returnedString = "returned string";

    StringBuilder builder = new StringBuilder();
    builder.append("public class MyClass {");
    builder.append("  public static String myMethod() {");
    builder.append("    return \"" + returnedString + "\";");
    builder.append("  }");
    builder.append("}");

    Path path = path("MyClass.java");
    fileSystem.createFileWithContent(path, builder.toString());

    script("run : [ file(path='" + path.value() + "') ] | javac | save(dir='.');");
    smoothRunner.run("run");

    problems.assertNoProblems();

    Class<?> klass = loadClass(byteCode(path("MyClass.class")));
    Object result = klass.getMethod("myMethod").invoke(null);
    assertThat(result).isEqualTo(returnedString);
  }

  private byte[] byteCode(Path classFilePath) throws IOException {
    InputStream inputStream = fileSystem.openInputStream(classFilePath);
    byte[] result = toByteArray(inputStream);
    inputStream.close();
    return result;
  }

  private static Class<?> loadClass(byte[] bytes) {
    return new MyClassLoader().defineClass(null, bytes);
  }

  private static class MyClassLoader extends ClassLoader {
    public Class<?> defineClass(String name, byte[] bytes) {
      return super.defineClass(name, bytes, 0, bytes.length);
    }
  }
}
