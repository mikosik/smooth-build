package org.smoothbuild.common.reflect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.reflect.ClassLoaders.mappingClassLoader;
import static org.smoothbuild.common.reflect.Classes.binaryPath;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ClassLoadersTest {
  @Test
  @SuppressWarnings("NullAway")
  void load_class() throws Exception {
    Class<MyClass> clazz = MyClass.class;
    String binaryName = clazz.getName();
    String binaryPath = binaryPath(clazz);
    try (var inputStream = clazz.getClassLoader().getResourceAsStream(binaryPath)) {
      assertThat(inputStream).isNotNull();
      // `null` forces JDK to use boot class loader which doesn't have access to MyClass
      // so when mapClassLoader asks its parent loader (boot class loader) for loading MyClass it
      // will receive nothing and then will be forced to load it by itself.
      ClassLoader parentClassLoader = null;
      var mappingClassLoader =
          mappingClassLoader(parentClassLoader, Map.of(binaryPath, inputStream)::get);

      Class<?> loadedClass = mappingClassLoader.loadClass(binaryName);
      assertThat(loadedClass.getClassLoader()).isSameInstanceAs(mappingClassLoader);
      assertThat(loadedClass.getMethod("myMethod").invoke(null)).isEqualTo("myResult");
    }
  }

  public static class MyClass {
    public static String myMethod() {
      return "myResult";
    }
  }

  @Test
  @SuppressWarnings("NullAway")
  void fails_for_missing_class() {
    var mapClassLoader = mappingClassLoader(Map.<String, InputStream>of()::get);
    assertCall(() -> mapClassLoader.loadClass("SomeClass"))
        .throwsException(new ClassNotFoundException("SomeClass"));
  }
}
