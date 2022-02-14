package org.smoothbuild.vm.java;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.nativefunc.NonPublicMethod;
import org.smoothbuild.testing.nativefunc.ReturnAbc;
import org.smoothbuild.util.collect.Result;

public class MethodLoaderTest extends TestingContext {
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
    var classLoaderProv = mock(JarClassLoaderProv.class);
    doReturn(Result.of(classLoader))
        .when(classLoaderProv)
        .classLoaderFor(jar);

    var methodLoader = new MethodLoader(classLoaderProv);
    Result<Method> method1 = methodLoader.provide(jar, className, "func");
    Result<Method> method2 = methodLoader.provide(jar, className, "func");
    assertThat(method1)
        .isSameInstanceAs(method2);
    verify(classLoader, times(1))
        .loadClass(className);
  }
}
