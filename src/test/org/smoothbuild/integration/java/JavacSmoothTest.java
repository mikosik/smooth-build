package org.smoothbuild.integration.java;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.builtin.java.javac.err.JavaCompilerError;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class JavacSmoothTest extends IntegrationTestCase {
  MyClassLoader classLoader = new MyClassLoader();

  @Test
  public void errorIsReportedForCompilationErrors() throws Exception {
    Path path = path("MyClass.java");
    fileSystem.createFileWithContent(path, "public private class MyClass {}");

    script("run : [ file(path='" + path.value() + "') ] | javac ;");
    smoothRunner.run("run");

    messages.assertOnlyProblem(JavaCompilerError.class);
  }

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

    messages.assertNoProblems();

    String classFile = "MyClass.class";
    String method = "myMethod";
    Object result = invoke(classFile, method);
    assertThat(result).isEqualTo(returnedString);
  }

  @Test
  public void compileOneFileWithLibraryDependency() throws Exception {
    {
      StringBuilder builder = new StringBuilder();
      builder.append("package library;");
      builder.append("public class LibraryClass {");
      builder.append("  public static int add(int a, int b) {");
      builder.append("    return a + b;");
      builder.append("  }");
      builder.append("}");

      Path path = path("library/LibraryClass.java");
      fileSystem.createFileWithContent(path, builder.toString());
    }
    {
      StringBuilder builder = new StringBuilder();
      builder.append("import library.LibraryClass;");
      builder.append("public class MyClass2 {");
      builder.append("  public static String myMethod() {");
      builder.append("    return Integer.toString(LibraryClass.add(2, 3));");
      builder.append("  }");
      builder.append("}");

      Path path = path("MyClass2.java");
      fileSystem.createFileWithContent(path, builder.toString());
    }
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("libraryClasses : [ file(path='library/LibraryClass.java') ] | javac ;");
    builder.addLine("libraryJar: libraryClasses | jar ;");
    builder.addLine("saveLibraryClass : libraryClasses | save('.');");
    builder
        .addLine("run : [ file(path='MyClass2.java') ] | javac(libs=[libraryJar]) | save(dir='.');");
    this.script(builder.build());

    smoothRunner.run("saveLibraryClass");
    smoothRunner.run("run");

    messages.assertNoProblems();

    loadClass(byteCode(path("library/LibraryClass.class")));
    String classFile = "MyClass2.class";
    String method = "myMethod";
    Object result = invoke(classFile, method);
    assertThat(result).isEqualTo("5");
  }

  private Object invoke(String classFile, String method) throws IOException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Class<?> klass = loadClass(byteCode(path(classFile)));
    return klass.getMethod(method).invoke(null);
  }

  private byte[] byteCode(Path classFilePath) throws IOException {
    InputStream inputStream = fileSystem.openInputStream(classFilePath);
    byte[] result = toByteArray(inputStream);
    inputStream.close();
    return result;
  }

  private Class<?> loadClass(byte[] bytes) {
    return classLoader.defineClass(null, bytes);
  }

  private static class MyClassLoader extends ClassLoader {
    public Class<?> defineClass(String name, byte[] bytes) {
      return super.defineClass(name, bytes, 0, bytes.length);
    }
  }
}
