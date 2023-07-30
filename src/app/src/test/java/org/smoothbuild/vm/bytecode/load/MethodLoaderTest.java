package org.smoothbuild.vm.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getPlatformClassLoader;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.vm.bytecode.load.NativeMethodLoader.NATIVE_METHOD_NAME;

import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.smoothbuild.common.collect.Try;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.nativ.MissingMethod;
import org.smoothbuild.testing.func.nativ.NonPublicMethod;
import org.smoothbuild.testing.func.nativ.OverloadedMethod;
import org.smoothbuild.testing.func.nativ.ReturnAbc;

public class MethodLoaderTest extends TestContext {
  @Test
  public void class_not_found_in_jar_error() throws Exception {
    var methodLoader = methodLoaderWithPlatformClassLoader();
    var methodSpec = new MethodSpec(blobBJarWithJavaByteCode(), "com.missing.Class", "methodName");
    assertThat(methodLoader.provide(methodSpec))
        .isEqualTo(Try.error("Class not found in jar."));
  }

  @Test
  public void overloaded_method_causes_error() throws Exception {
    var clazz = OverloadedMethod.class;
    var methodLoader = methodLoaderWithPlatformClassLoader();
    var methodSpec = methodSpec(clazz);
    assertThat(methodLoader.provide(methodSpec))
        .isEqualTo(loadingError(clazz, "has more than one 'func' method."));
  }

  @Test
  public void missing_method_causes_error() throws Exception {
    var clazz = MissingMethod.class;
    var methodLoader = methodLoaderWithPlatformClassLoader();
    var methodSpec = methodSpec(clazz);
    assertThat(methodLoader.provide(methodSpec))
        .isEqualTo(loadingError(clazz, "does not have 'func' method."));
  }

  private MethodSpec methodSpec(Class<?> clazz) throws IOException {
    var jar = blobBJarWithPluginApi(clazz);
    return new MethodSpec(jar, clazz.getCanonicalName(), NATIVE_METHOD_NAME);
  }

  private Try<Object> loadingError(Class<?> clazz, String message) {
    return Try.error("Class '" + clazz.getCanonicalName() + "' " + message);
  }

  private MethodLoader methodLoaderWithPlatformClassLoader() {
    return new MethodLoader(new JarClassLoaderProv(bytecodeF(), getPlatformClassLoader()));
  }

  @Nested
  class _caching {
    @Test
    public void method_is_cached() throws Exception {
      testCaching(ReturnAbc.class);
    }

    @Test
    public void loading_method_error_is_cached() throws Exception {
      testCaching(NonPublicMethod.class);
    }

    private void testCaching(Class<?> clazz) throws Exception {
      var className = "className";
      var jar = blobB();

      var classLoader = mock(ClassLoader.class);
      doReturn(clazz)
          .when(classLoader)
          .loadClass(className);
      var classLoaderProv = Mockito.mock(JarClassLoaderProv.class);
      doReturn(Try.result(classLoader))
          .when(classLoaderProv)
          .classLoaderFor(jar);

      var methodLoader = new MethodLoader(classLoaderProv);
      var methodSpec = new MethodSpec(jar, className, "func");
      Try<Method> methodTry1 = methodLoader.provide(methodSpec);
      Try<Method> methodTry2 = methodLoader.provide(methodSpec);
      assertThat(methodTry1)
          .isSameInstanceAs(methodTry2);
      verify(classLoader, times(1))
          .loadClass(className);
    }
  }
}
