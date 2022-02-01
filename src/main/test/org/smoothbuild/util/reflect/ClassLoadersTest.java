package org.smoothbuild.util.reflect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.reflect.ClassLoaders.mapClassLoader;
import static org.smoothbuild.util.reflect.Classes.binaryPath;

import java.io.InputStream;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class ClassLoadersTest extends TestingContext {
  @Test
  public void load_class() throws Exception {
    Class<MyClass> clazz = MyClass.class;
    String binaryName = clazz.getName();
    String binaryPath = binaryPath(clazz);
    try (var inputStream = clazz.getClassLoader().getResourceAsStream(binaryPath)) {
      assertThat(inputStream)
          .isNotNull();
      // `null` forces JDK to use boot class loader which doesn't have access to MyClass
      // so when mapClassLoader asks its parent loader (boot class loader) for loading MyClass it
      // will receive nothing and then will be forced to load it by itself.
      ClassLoader parentClassLoader = null;
      var mapClassLoader = mapClassLoader(parentClassLoader, Map.of(binaryPath, inputStream)::get);

      Class<?> loadedClass = mapClassLoader.loadClass(binaryName);
      assertThat(loadedClass.getClassLoader())
          .isSameInstanceAs(mapClassLoader);
      assertThat(loadedClass.getMethod("myMethod").invoke(null))
          .isEqualTo("myResult");
    }
  }

  public static class MyClass {
    public static String myMethod() {
      return "myResult";
    }
  }

  @Test
  public void fails_for_missing_class() {
    var mapClassLoader = mapClassLoader(Map.<String, InputStream>of()::get);
    assertCall(() -> mapClassLoader.loadClass("SomeClass"))
        .throwsException(new ClassNotFoundException("SomeClass"));
  }
}
