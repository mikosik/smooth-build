package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getPlatformClassLoader;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.MissingMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.NonPublicMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.OverloadedMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnAbc;

public class MethodLoaderTest extends VmTestContext {
  @Test
  void class_not_found_in_jar_error() throws Exception {
    var methodLoader = methodLoaderWithPlatformClassLoader();
    var bBlob = blobBJarWithJavaByteCode();
    var bMethod = bMethod(bBlob, "com.missing.Class", "methodName");
    assertThat(methodLoader.load(bMethod)).isEqualTo(err("Class not found in jar."));
  }

  @Test
  void overloaded_method_causes_error() throws Exception {
    var clazz = OverloadedMethod.class;
    var methodLoader = methodLoaderWithPlatformClassLoader();
    var bMethod = bMethod(clazz);
    assertThat(methodLoader.load(bMethod))
        .isEqualTo(loadingError(clazz, "has more than one 'func' method."));
  }

  @Test
  void missing_method_causes_error() throws Exception {
    var clazz = MissingMethod.class;
    var methodLoader = methodLoaderWithPlatformClassLoader();
    var bMethod = bMethod(clazz);
    assertThat(methodLoader.load(bMethod))
        .isEqualTo(loadingError(clazz, "does not have 'func' method."));
  }

  private Result<Object> loadingError(Class<?> clazz, String message) {
    return err("Class '" + clazz.getCanonicalName() + "' " + message);
  }

  private MethodLoader methodLoaderWithPlatformClassLoader() {
    return new MethodLoader(
        new JarClassLoaderFactory(provide().bytecodeFactory(), getPlatformClassLoader()));
  }

  @Nested
  class _caching {
    @Test
    void method_is_cached() throws Exception {
      testCaching(ReturnAbc.class);
    }

    @Test
    void loading_method_error_is_cached() throws Exception {
      testCaching(NonPublicMethod.class);
    }

    private void testCaching(Class<?> clazz) throws Exception {
      var className = "className";
      var jar = bBlob();
      var bMethod = bMethod(jar, className);

      var classLoader = mock(ClassLoader.class);
      doReturn(clazz).when(classLoader).loadClass(className);
      var classLoaderFactory = mock(JarClassLoaderFactory.class);
      doReturn(ok(classLoader)).when(classLoaderFactory).classLoaderFor(jar);

      var methodLoader = new MethodLoader(classLoaderFactory);
      Result<Method> method1 = methodLoader.load(bMethod);
      Result<Method> method2 = methodLoader.load(bMethod);
      assertThat(method1).isSameInstanceAs(method2);
      verify(classLoader, times(1)).loadClass(className);
    }
  }
}
