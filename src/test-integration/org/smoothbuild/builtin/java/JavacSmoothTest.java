package org.smoothbuild.builtin.java;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.builtin.java.javac.err.IllegalSourceParamError;
import org.smoothbuild.builtin.java.javac.err.IllegalTargetParamError;
import org.smoothbuild.builtin.java.javac.err.JavaCompilerMessage;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class JavacSmoothTest extends IntegrationTestCase {
  MyClassLoader classLoader = new MyClassLoader();

  @Test
  public void errorIsReportedForCompilationErrors() throws Exception {
    Path path = path("MyClass.java");
    fileSystem.createFile(path, "public private class MyClass {}");

    script("run : [ file(path=" + path + ") ] | javac ;");
    smoothApp.run("run");

    messages.assertOnlyProblem(JavaCompilerMessage.class);
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
    fileSystem.createFile(path, builder.toString());

    script("run : [ file(path=" + path + ") ] | javac | save(dir='.');");
    smoothApp.run("run");

    messages.assertNoProblems();

    String classFile = "MyClass.class";
    String method = "myMethod";
    Object result = invoke(path(classFile), method);
    assertThat(result).isEqualTo(returnedString);
  }

  @Test
  public void compileOneFileWithLibraryDependency() throws Exception {
    Path libSourceDir = path("src/lib");
    Path appSourceDir = path("src/app");
    Path classDir = path("class");

    Path libraryJavaFile = libSourceDir.append(path("library/LibraryClass.java"));
    Path appJavaFile = appSourceDir.append(path("MyClass2.java"));
    Path libClassFile = classDir.append(path("library/LibraryClass.class"));
    Path appClassFile = classDir.append(path("MyClass2.class"));

    {
      StringBuilder builder = new StringBuilder();
      builder.append("package library;");
      builder.append("public class LibraryClass {");
      builder.append("  public static int add(int a, int b) {");
      builder.append("    return a + b;");
      builder.append("  }");
      builder.append("}");

      fileSystem.createFile(libraryJavaFile, builder.toString());
    }
    {
      StringBuilder builder = new StringBuilder();
      builder.append("import library.LibraryClass;");
      builder.append("public class MyClass2 {");
      builder.append("  public static String myMethod() {");
      builder.append("    return Integer.toString(LibraryClass.add(2, 3));");
      builder.append("  }");
      builder.append("}");

      fileSystem.createFile(appJavaFile, builder.toString());
    }

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("libraryClasses : [ file(path=" + libraryJavaFile + ") ] | javac ;");
    builder.addLine("libraryJar: libraryClasses | jar ;");
    builder.addLine("appClasses: [ file(path=" + appJavaFile + ") ] | javac(libs=[libraryJar]) ;");
    builder.addLine("run: libraryClasses | merge(with=appClasses) | save(" + classDir + ");");
    this.script(builder.build());

    smoothApp.run("run");

    messages.assertNoProblems();
    loadClass(byteCode(libClassFile));
    String method = "myMethod";
    Object result = invoke(appClassFile, method);
    assertThat(result).isEqualTo("5");
  }

  @Test
  public void illegalSourceParamCausesError() throws Exception {
    Path path = path("MyClass.java");
    fileSystem.createFile(path, "public class MyClass {}");

    script("run : [ file(path=" + path + ") ] | javac(source='0.9') ;");
    smoothApp.run("run");

    messages.assertOnlyProblem(IllegalSourceParamError.class);
  }

  @Test
  public void illegalTargetParamCausesError() throws Exception {
    Path path = path("MyClass.java");
    fileSystem.createFile(path, "public class MyClass {}");

    script("run : [ file(path=" + path + ") ] | javac(target='0.9') ;");
    smoothApp.run("run");

    messages.assertOnlyProblem(IllegalTargetParamError.class);
  }

  @Test
  public void compilingEnumWithSourceParamSetToTooOldJavaVersionCausesError() throws Exception {
    Path path = path("MyClass.java");
    fileSystem.createFile(path, "public enum MyClass { VALUE }");

    script("run : [ file(path=" + path + ") ] | javac(source='1.4', target='1.4') ;");
    smoothApp.run("run");

    messages.assertOnlyProblem(JavaCompilerMessage.class);
  }

  private Object invoke(Path appClassFile, String method) throws IOException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Class<?> klass = loadClass(byteCode(appClassFile));
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
