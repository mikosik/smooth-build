package org.smoothbuild.builtin.java;

import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.ARTIFACTS_PATH;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.builtin.java.javac.err.IllegalSourceParamError;
import org.smoothbuild.builtin.java.javac.err.IllegalTargetParamError;
import org.smoothbuild.builtin.java.javac.err.JavaCompilerMessage;
import org.smoothbuild.builtin.java.javac.err.NoJavaSourceFilesFoundWarning;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;
import org.smoothbuild.util.LineBuilder;

public class JavacSmoothTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  MyClassLoader classLoader = new MyClassLoader();

  @Test
  public void errorIsLoggededForCompilationErrors() throws Exception {
    Path path = path("MyClass.java");
    fileSystem.createFile(path, "public private class MyClass {}");

    script(fileSystem, "run : [ file(path=" + path + ") ] | javac ;");
    buildWorker.run(asList("run"));

    userConsole.messages().assertContainsOnly(JavaCompilerMessage.class);
  }

  @Test
  public void compileZeroFiles() throws Exception {
    script(fileSystem, "run : [ ] | javac ;");
    buildWorker.run(asList("run"));

    userConsole.messages().assertContainsOnly(NoJavaSourceFilesFoundWarning.class);
  }

  @Test
  public void compileOneFile() throws Exception {
    String returnedString = "returned string";

    LineBuilder builder = new LineBuilder();
    builder.addLine("public class MyClass {");
    builder.addLine("  public static String myMethod() {");
    builder.addLine("    return \"" + returnedString + "\";");
    builder.addLine("  }");
    builder.addLine("}");

    Path path = path("MyClass.java");
    fileSystem.createFile(path, builder.build());

    script(fileSystem, "run : [ file(path=" + path + ") ] | javac ;");
    buildWorker.run(asList("run"));

    userConsole.messages().assertNoProblems();

    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    Path classFile = artifactPath.append(path("MyClass.class"));
    Object result = invoke(classFile, "myMethod");
    assertThat(result).isEqualTo(returnedString);
  }

  @Test
  public void compileOneFileWithLibraryDependency() throws Exception {
    Path libSourceDir = path("src/lib");
    Path appSourceDir = path("src/app");

    Path libraryJavaFile = libSourceDir.append(path("library/LibraryClass.java"));
    Path appJavaFile = appSourceDir.append(path("MyClass2.java"));

    {
      LineBuilder builder = new LineBuilder();
      builder.addLine("package library;");
      builder.addLine("public class LibraryClass {");
      builder.addLine("  public static int add(int a, int b) {");
      builder.addLine("    return a + b;");
      builder.addLine("  }");
      builder.addLine("}");

      fileSystem.createFile(libraryJavaFile, builder.build());
    }
    {
      LineBuilder builder = new LineBuilder();
      builder.addLine("import library.LibraryClass;");
      builder.addLine("public class MyClass2 {");
      builder.addLine("  public static String myMethod() {");
      builder.addLine("    return Integer.toString(LibraryClass.add(2, 3));");
      builder.addLine("  }");
      builder.addLine("}");

      fileSystem.createFile(appJavaFile, builder.build());
    }

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("libraryClasses : [ file(path=" + libraryJavaFile + ") ] | javac ;");
    builder.addLine("libraryJar: libraryClasses | jar ;");
    builder.addLine("appClasses: [ file(path=" + appJavaFile + ") ] | javac(libs=[libraryJar]) ;");
    builder.addLine("run: libraryClasses | concatenateFiles(with=appClasses) ;");
    script(fileSystem, builder.build());

    buildWorker.run(asList("run"));

    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    Path libClassFile = artifactPath.append(path("library/LibraryClass.class"));
    Path appClassFile = artifactPath.append(path("MyClass2.class"));

    userConsole.messages().assertNoProblems();
    loadClass(byteCode(libClassFile));
    String method = "myMethod";
    Object result = invoke(appClassFile, method);
    assertThat(result).isEqualTo("5");
  }

  @Test
  public void duplicate_java_files_cause_error() throws Exception {
    Path path = path("MyClass.java");
    fileSystem.createFile(path, "public class MyClass {}");

    script(fileSystem, "run : [ file(" + path + "), file(" + path + ") ] | javac ;");
    buildWorker.run(asList("run"));

    userConsole.messages().assertContainsOnly(JavaCompilerMessage.class);
  }

  @Test
  public void illegalSourceParamCausesError() throws Exception {
    Path path = path("MyClass.java");
    fileSystem.createFile(path, "public class MyClass {}");

    script(fileSystem, "run : [ file(path=" + path + ") ] | javac(source='0.9') ;");
    buildWorker.run(asList("run"));

    userConsole.messages().assertContainsOnly(IllegalSourceParamError.class);
  }

  @Test
  public void illegalTargetParamCausesError() throws Exception {
    Path path = path("MyClass.java");
    fileSystem.createFile(path, "public class MyClass {}");

    script(fileSystem, "run : [ file(path=" + path + ") ] | javac(target='0.9') ;");
    buildWorker.run(asList("run"));

    userConsole.messages().assertContainsOnly(IllegalTargetParamError.class);
  }

  @Test
  public void compilingEnumWithSourceParamSetToTooOldJavaVersionCausesError() throws Exception {
    Path path = path("MyClass.java");
    fileSystem.createFile(path, "public enum MyClass { VALUE }");

    script(fileSystem, "run : [ file(path=" + path + ") ] | javac(source='1.4', target='1.4') ;");
    buildWorker.run(asList("run"));

    userConsole.messages().assertContainsOnly(JavaCompilerMessage.class);
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
