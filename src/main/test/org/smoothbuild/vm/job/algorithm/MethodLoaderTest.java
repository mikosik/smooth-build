package org.smoothbuild.vm.job.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.job.algorithm.MethodLoader.NATIVE_METHOD_NAME;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.nativefunc.NonPublicMethod;
import org.smoothbuild.testing.nativefunc.ReturnAbc;
import org.smoothbuild.util.collect.Result;
import org.smoothbuild.vm.java.MethodProv;

public class MethodLoaderTest extends TestingContext {
  @Test
  public void method_is_cached() throws Exception {
    var method = ReturnAbc.class.getDeclaredMethod(NATIVE_METHOD_NAME, NativeApi.class);
    testCaching(method, Result.of(method), Result.of(method));
  }

  @Test
  public void error_when_loading_method_is_cached() throws Exception {
    var method = NonPublicMethod.class.getDeclaredMethod(NATIVE_METHOD_NAME, NativeApi.class);
    testCaching(method, Result.error("xx"), Result.error(
        "Error loading native implementation for `smoothName` specified as `binary.name`: xx"));
  }

  private void testCaching(Method method, Result<Method> resultMethod, Result<Method> expected) {
    var methodProv = mock(MethodProv.class);
    BlobB jar = blobB();
    String classBinaryName = "binary.name";
    when(methodProv.provide(jar, classBinaryName, method.getName()))
        .thenReturn(resultMethod);

    var methodLoader = new MethodLoader(methodProv);

    var methodB = methodB(methodTB(stringTB(), list()), jar, stringB(classBinaryName));
    var resultMethod1 = methodLoader.load("smoothName", methodB);
    var resultMethod2 = methodLoader.load("smoothName", methodB);
    assertThat(resultMethod1)
        .isEqualTo(expected);
    assertThat(resultMethod1)
        .isSameInstanceAs(resultMethod2);
    verify(methodProv, times(1))
        .provide(jar, classBinaryName, method.getName());
  }
}
