package org.smoothbuild.systemtest.slib.java;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.util.io.Okios.readAndClose;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class JavacTest extends SystemTestCase {
  @Test
  public void error_is_logged_when_compilation_error_occurs() throws Exception {
    createUserModule("""
            result = [File(toBlob("public private class MyClass {}"), "MyClass.java")]
              > javac();
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("modifier private not allowed here");
  }

  @Test
  public void zero_files_can_be_compiled() throws Exception {
    createUserModule("""
            result = [] > javac();
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("Param 'srcs' is empty list.");
  }

  @Test
  public void one_file_can_be_compiled() throws Exception {
    String classSource = "public class MyClass { "
        + "public static String myMethod() {return \\\"test-string\\\";}}";
    createUserModule(
        "  result = [File(toBlob(\"" + classSource + "\"), \"MyClass.java\")] > javac();  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(invoke(artifactAbsolutePath("result").resolve("MyClass.class"), "myMethod"))
        .isEqualTo("test-string");
  }

  @Test
  public void one_file_with_library_dependency_can_be_compiled() throws Exception {
    createFile("src/MyClass.java", """
        import library.LibraryClass;
        public class MyClass {
          public static String myMethod() {
            return Integer.toString(LibraryClass.add(2, 3));
          }
        }
        """);
    createFile("srclib/library/LibraryClass.java", """
        package library;
        public class LibraryClass {
          public static int add(int a, int b) {
            return a + b;
          }
        }
        """);
    createUserModule("""
            libraryJar = projectFiles("srclib") > javac() > jar() > File("library.jar");
            result = concat([(projectFiles("src") > javac(libs = [libraryJar])), javac(projectFiles("srclib"))]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();

    Path libraryClassFile = artifactAbsolutePath("result").resolve("library/LibraryClass.class");
    Path classFile = artifactAbsolutePath("result").resolve("MyClass.class");
    MyClassLoader classLoader = new MyClassLoader();
    loadClass(classLoader, bytecode(libraryClassFile.toFile()));
    assertThat(invoke(classLoader, classFile.toFile(), "myMethod"))
        .isEqualTo("5");
  }

  @Test
  public void duplicate_java_files_cause_error() throws Exception {
    createUserModule("""
            classFile = File(toBlob("public class MyClass {}"), "MyClass.java");
            result = [classFile, classFile] > javac();
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("duplicate class: MyClass");
  }

  @Test
  public void illegal_source_param_causes_error() throws Exception {
    createUserModule("""
            result = [File(toBlob("public class MyClass {}"), "MyClass.java")]
              > javac(source="0.9");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("invalid source release: 0.9");
  }

  @Test
  public void illegal_target_param_causes_error() throws Exception {
    createUserModule("""
            result = [File(toBlob("public class MyClass {}"), "MyClass.java")]
              > javac(target="0.9");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("invalid target release: 0.9");
  }

  @Test
  public void compiling_enum_with_source_param_set_to_too_old_java_version_causes_error()
      throws Exception {
    createUserModule("""
            result = [File(toBlob("public enum MyClass { VALUE }"), "MyClass.java")]
              > javac(source="1.4", target="1.4");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Source option 1.4 is no longer supported.");
  }

  private Object invoke(Path appClassFile, String method) throws IOException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    return invoke(new MyClassLoader(), appClassFile.toFile(), method);
  }

  private Object invoke(MyClassLoader classLoader, File appClassFile, String method)
      throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Class<?> klass = loadClass(classLoader, bytecode(appClassFile));
    return klass.getMethod(method).invoke(null);
  }

  private byte[] bytecode(File classFilePath) throws IOException {
    return readAndClose(buffer(source(classFilePath)), s -> s.readByteArray());
  }

  private Class<?> loadClass(MyClassLoader classLoader, byte[] bytes) {
    return classLoader.defineClass(null, bytes);
  }

  private static class MyClassLoader extends ClassLoader {
    public Class<?> defineClass(String name, byte[] bytes) {
      return super.defineClass(name, bytes, 0, bytes.length);
    }
  }
}
