package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getPlatformClassLoader;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader.NATIVE_METHOD_NAME;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;
import org.smoothbuild.virtualmachine.testing.func.nativ.MissingMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.NonPublicMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.OverloadedMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnAbc;

public class MethodLoaderTest extends TestVirtualMachine {
  @Test
  public void class_not_found_in_jar_error() throws Exception {
    var methodLoader = methodLoaderWithPlatformClassLoader();
    var methodSpec = new MethodSpec(blobBJarWithJavaByteCode(), "com.missing.Class", "methodName");
    assertThat(methodLoader.load(methodSpec)).isEqualTo(left("Class not found in jar."));
  }

  @Test
  public void overloaded_method_causes_error() throws Exception {
    var clazz = OverloadedMethod.class;
    var methodLoader = methodLoaderWithPlatformClassLoader();
    var methodSpec = methodSpec(clazz);
    assertThat(methodLoader.load(methodSpec))
        .isEqualTo(loadingError(clazz, "has more than one 'func' method."));
  }

  @Test
  public void missing_method_causes_error() throws Exception {
    var clazz = MissingMethod.class;
    var methodLoader = methodLoaderWithPlatformClassLoader();
    var methodSpec = methodSpec(clazz);
    assertThat(methodLoader.load(methodSpec))
        .isEqualTo(loadingError(clazz, "does not have 'func' method."));
  }

  private MethodSpec methodSpec(Class<?> clazz) throws BytecodeException {
    var jar = blobBJarWithPluginApi(clazz);
    return new MethodSpec(jar, clazz.getCanonicalName(), NATIVE_METHOD_NAME);
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
      doReturn(clazz).when(classLoader).loadClass(className);
      var classLoaderFactory = mock(JarClassLoaderFactory.class);
      doReturn(right(classLoader)).when(classLoaderFactory).classLoaderFor(jar);

      var methodLoader = new MethodLoader(classLoaderFactory);
      var methodSpec = new MethodSpec(jar, className, "func");
      Either<String, Method> methodEither1 = methodLoader.load(methodSpec);
      Either<String, Method> methodEither2 = methodLoader.load(methodSpec);
      assertThat(methodEither1).isSameInstanceAs(methodEither2);
      verify(classLoader, times(1)).loadClass(className);
    }
  }
}
