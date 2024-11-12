package org.smoothbuild.stdlib.java;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.warning;

import java.lang.reflect.InvocationTargetException;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.stdlib.StandardLibraryTestContext;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.helper.FileStruct;

public class JavacTest extends StandardLibraryTestContext {
  @Test
  void error_is_logged_when_compilation_error_occurs() throws Exception {
    var userModule =
        """
        result = [File(toBlob("public private class MyClass {}"), "MyClass.java")]
          > javac();
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs()).contains(error("modifier private not allowed here"));
  }

  @Test
  void zero_files_can_be_compiled() throws Exception {
    var userModule = """
        result = [] > javac();
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs()).contains(warning("Parameter 'srcs' is empty list."));
  }

  @Test
  void one_file_can_be_compiled() throws Exception {
    var userModule =
        """
        javaSource = "public class MyClass { public static String myMethod() {return \\"test-string\\";}}";
        result = [File(toBlob(javaSource), "MyClass.java")] > javac();
        """;
    createUserModule(userModule);

    evaluate("result");

    var map = arrayToFileMap(artifact());
    assertThat(invoke(map.get("MyClass.class"), "myMethod")).isEqualTo("test-string");
  }

  @Test
  void one_file_with_library_dependency_can_be_compiled() throws Exception {
    var userModule =
        """
        libraryJar = files("srclib") > javac() > jar() > File("library.jar");
        result = concat([(files("src") > javac(libs = [libraryJar])), javac(files("srclib"))]);
        """;
    createUserModule(userModule);
    createProjectFile(
        "src/MyClass.java",
        """
        import library.LibraryClass;
        public class MyClass {
          public static String myMethod() {
            return Integer.toString(LibraryClass.add(2, 3));
          }
        }
        """);
    createProjectFile(
        "srclib/library/LibraryClass.java",
        """
        package library;
        public class LibraryClass {
          public static int add(int a, int b) {
            return a + b;
          }
        }
        """);
    evaluate("result");

    var map = arrayToFileMap(artifact());
    var classLoader = new MyClassLoader();
    loadClass(classLoader, map.get("library/LibraryClass.class"));
    assertThat(invoke(classLoader, map.get("MyClass.class"), "myMethod")).isEqualTo("5");
  }

  @Test
  void duplicate_java_files_cause_error() throws Exception {
    var userModule =
        """
        classFile = File(toBlob("public class MyClass {}"), "MyClass.java");
        result = [classFile, classFile] > javac();
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs()).contains(error("duplicate class: MyClass"));
  }

  private static Map<String, ByteString> arrayToFileMap(BValue array) throws Exception {
    List<BTuple> elements = ((BArray) array).elements(BTuple.class);
    return elements.toMap(JavacTest::fileBToStringPath, JavacTest::fileBToByteStringContent);
  }

  private static ByteString fileBToByteStringContent(BTuple file) throws Exception {
    try (var source = buffer(FileStruct.fileContent(file).source())) {
      return source.readByteString();
    }
  }

  private static String fileBToStringPath(BTuple file) throws BytecodeException {
    return FileStruct.filePath(file).toJavaString();
  }

  private Object invoke(ByteString classBytes, String method)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    return invoke(new MyClassLoader(), classBytes, method);
  }

  private Object invoke(MyClassLoader classLoader, ByteString classBytes, String method)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Class<?> klass = loadClass(classLoader, classBytes);
    return klass.getMethod(method).invoke(null);
  }

  private Class<?> loadClass(MyClassLoader classLoader, ByteString bytes) {
    return classLoader.defineClass(null, bytes.toByteArray());
  }

  private static class MyClassLoader extends ClassLoader {
    public Class<?> defineClass(String name, byte[] bytes) {
      return super.defineClass(name, bytes, 0, bytes.length);
    }
  }
}
