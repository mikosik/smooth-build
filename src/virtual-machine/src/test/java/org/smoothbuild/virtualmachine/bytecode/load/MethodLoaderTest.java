package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getPlatformClassLoader;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.MissingMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.NonPublicMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.OverloadedMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnAbc;

public class MethodLoaderTest extends BytecodeTestContext {
  @Test
  void class_not_found_in_jar_error() throws Exception {
    var methodLoader = methodLoaderWithPlatformClassLoader();
    var bBlob = blobBJarWithJavaByteCode();
    var bMethod = bMethod(bBlob, "com.missing.Class", "methodName");
    assertThat(methodLoader.load(bMethod)).isEqualTo(left("Class not found in jar."));
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

  private Either<String, Object> loadingError(Class<?> clazz, String message) {
    return left("Class '" + clazz.getCanonicalName() + "' " + message);
  }

  private MethodLoader methodLoaderWithPlatformClassLoader() {
    return new MethodLoader(new JarClassLoaderFactory(bytecodeF(), getPlatformClassLoader()));
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
      doReturn(right(classLoader)).when(classLoaderFactory).classLoaderFor(jar);

      var methodLoader = new MethodLoader(classLoaderFactory);
      Either<String, Method> methodEither1 = methodLoader.load(bMethod);
      Either<String, Method> methodEither2 = methodLoader.load(bMethod);
      assertThat(methodEither1).isSameInstanceAs(methodEither2);
      verify(classLoader, times(1)).loadClass(className);
    }
  }
}
