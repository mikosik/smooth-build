package org.smoothbuild.acceptance.builtin.java;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.util.Okios.readAndClose;
import static org.testory.Testory.thenEqual;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class JavacTest extends AcceptanceTestCase {
  @Test
  public void error_is_logged_when_compilation_error_occurs() throws Exception {
    givenFile("MyClass.java", "public private class MyClass {}");
    givenScript("result = [file('//MyClass.java')] | javac;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("modifier private not allowed here");
  }

  @Test
  public void zero_files_can_be_compiled() throws Exception {
    givenScript("result = [] | javac;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenOutputContains("Param 'srcs' is empty list.");
  }

  @Test
  public void one_file_can_be_compiled() throws Exception {
    givenFile("MyClass.java", "public class MyClass {\n"
        + "public static String myMethod() {return \"test-string\";}}");
    givenScript("result = [file('//MyClass.java')] | javac;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual("test-string", invoke(new File(artifact("result"), "MyClass.class"), "myMethod"));
  }

  @Test
  public void one_file_with_library_dependency_can_be_compiled() throws Exception {
    StringBuilder classSource = new StringBuilder();
    classSource.append("import library.LibraryClass;");
    classSource.append("public class MyClass {");
    classSource.append("  public static String myMethod() {");
    classSource.append("    return Integer.toString(LibraryClass.add(2, 3));");
    classSource.append("  }");
    classSource.append("}");

    StringBuilder librarySource = new StringBuilder();
    librarySource.append("package library;");
    librarySource.append("public class LibraryClass {");
    librarySource.append("  public static int add(int a, int b) {");
    librarySource.append("    return a + b;");
    librarySource.append("  }");
    librarySource.append("}");

    givenFile("src/MyClass.java", classSource.toString());
    givenFile("srclib/library/LibraryClass.java", librarySource.toString());
    givenScript("libraryJar = files('//srclib') | javac | jar;"
        + "result = files('//src') | javac(libs=[libraryJar])"
        + " | concatenate(array2=javac(files('//srclib')));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();

    File libraryClassFile = new File(artifact("result"), "library/LibraryClass.class");
    File classFile = new File(artifact("result"), "MyClass.class");
    MyClassLoader classLoader = new MyClassLoader();
    loadClass(classLoader, byteCode(libraryClassFile));
    thenEqual("5", invoke(classLoader, classFile, "myMethod"));
  }

  @Test
  public void duplicate_java_files_cause_error() throws Exception {
    givenFile("MyClass.java", "public class MyClass {}");
    givenScript("result = [file('//MyClass.java'), file('//MyClass.java')] | javac;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("duplicate class: MyClass");
  }

  @Test
  public void illegal_source_parameter_causes_error() throws Exception {
    givenFile("MyClass.java", "public class MyClass {}");
    givenScript("result = [file('//MyClass.java')] | javac(source='0.9');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("invalid source release: 0.9");
  }

  @Test
  public void illegal_target_parameter_causes_error() throws Exception {
    givenFile("MyClass.java", "public class MyClass {}");
    givenScript("result = [file('//MyClass.java')] | javac(target='0.9');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("invalid target release: 0.9");
  }

  @Test
  public void compiling_enum_with_source_parameter_set_to_too_old_java_version_causes_error()
      throws Exception {
    givenFile("MyClass.java", "public enum MyClass { VALUE }");
    givenScript("result = [file('//MyClass.java')] | javac(source='1.4', target='1.4');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("enums are not supported in -source 1.4");
  }

  private Object invoke(File appClassFile, String method) throws IOException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    return invoke(new MyClassLoader(), appClassFile, method);
  }

  private Object invoke(MyClassLoader classLoader, File appClassFile, String method)
      throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Class<?> klass = loadClass(classLoader, byteCode(appClassFile));
    return klass.getMethod(method).invoke(null);
  }

  private byte[] byteCode(File classFilePath) throws IOException {
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
