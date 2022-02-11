package org.smoothbuild.vm.java;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.nativefunc.ReturnAbc;

public class MethodProvTest extends TestingContext {
  @Test
  public void method_is_cached() throws Exception {
    var className = "className";
    var jar = blobB();

    var classLoader = mock(ClassLoader.class);
    doReturn(ReturnAbc.class)
        .when(classLoader)
        .loadClass(className);
    var classLoaderProv = mock(ClassLoaderProv.class);
    doReturn(classLoader)
        .when(classLoaderProv)
        .classLoaderFor(jar);

    var methodLoader = new MethodProv(classLoaderProv);
    assertThat(methodLoader.provide(jar, className, "func"))
        .isSameInstanceAs(methodLoader.provide(jar, className, "func"));
    verify(classLoader, times(1))
        .loadClass(className);
  }
}
