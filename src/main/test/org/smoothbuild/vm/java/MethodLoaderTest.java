package org.smoothbuild.vm.java;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.java.MethodLoader.NATIVE_METHOD_NAME;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.nativefunc.ReturnAbc;
import org.smoothbuild.testing.nativefunc.ReturnAbcdef;

public class MethodLoaderTest extends TestingContext {
  @Test
  public void method_is_cached() throws Exception {
    var className = "className";
    var jar = blobB();
    var methodB = methodB(methodTB(stringTB(), list()), jar, stringB(className));

    var classLoader = mock(ClassLoader.class);
    doReturn(ReturnAbc.class)
        .when(classLoader)
        .loadClass(className);
    var classLoaderProv = mock(ClassLoaderProv.class);
    doReturn(classLoader)
        .when(classLoaderProv)
        .classLoaderForJar(jar);

    var methodLoader = new MethodLoader();
    assertThat(methodLoader.load("", methodB, classLoaderProv))
        .isSameInstanceAs(methodLoader.load("", methodB, classLoaderProv));
    verify(classLoader, times(1))
        .loadClass(className);
  }

  @Test
  public void classloader_is_cached() throws Exception {
    var className1 = "className1";
    var className2 = "className2";
    var jar = blobB();
    var methodB1 = methodB(methodTB(stringTB(), list()), jar, stringB(className1));
    var methodB2 = methodB(methodTB(stringTB(), list()), jar, stringB(className2));

    var classLoader = mock(ClassLoader.class);
    doReturn(ReturnAbc.class)
        .when(classLoader)
        .loadClass(className1);
    doReturn(ReturnAbcdef.class)
        .when(classLoader)
        .loadClass(className2);
    var classLoaderProv = mock(ClassLoaderProv.class);
    doReturn(classLoader)
        .when(classLoaderProv)
        .classLoaderForJar(jar);

    var methodLoader = new MethodLoader();
    assertThat(methodLoader.load("", methodB1, classLoaderProv))
        .isEqualTo(getMethod(ReturnAbc.class));
    assertThat(methodLoader.load("", methodB2, classLoaderProv))
        .isEqualTo(getMethod(ReturnAbcdef.class));

    verify(classLoaderProv, times(1))
        .classLoaderForJar(jar);
  }

  private Method getMethod(Class<?> clazz) throws NoSuchMethodException {
    return clazz.getMethod(NATIVE_METHOD_NAME, NativeApi.class);
  }
}
