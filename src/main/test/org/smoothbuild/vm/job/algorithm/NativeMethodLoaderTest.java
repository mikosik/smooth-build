package org.smoothbuild.vm.job.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.job.algorithm.NativeMethodLoader.NATIVE_METHOD_NAME;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.load.MethodLoader;
import org.smoothbuild.load.MethodSpec;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.func.nativ.NonPublicMethod;
import org.smoothbuild.testing.func.nativ.ReturnAbc;
import org.smoothbuild.util.collect.Try;

public class NativeMethodLoaderTest extends TestingContext {
  @Test
  public void method_is_cached() throws Exception {
    var method = ReturnAbc.class.getDeclaredMethod(
        NATIVE_METHOD_NAME, NativeApi.class, TupleB.class);
    testCaching(method, Try.result(method), Try.result(method));
  }

  @Test
  public void error_when_loading_method_is_cached() throws Exception {
    var method = NonPublicMethod.class.getDeclaredMethod(
        NATIVE_METHOD_NAME, NativeApi.class, TupleB.class);
    testCaching(method, Try.error("xx"), Try.error(
        "Error loading native implementation for `smoothName` specified as `binary.name`: xx"));
  }

  private void testCaching(Method method, Try<Method> tryMethod, Try<Method> expected) {
    var methodLoader = mock(MethodLoader.class);
    var jar = blobB();
    var classBinaryName = "binary.name";
    var methodSpec = new MethodSpec(jar, classBinaryName, method.getName());
    when(methodLoader.provide(methodSpec))
        .thenReturn(tryMethod);

    var nativeMethodLoader = new NativeMethodLoader(methodLoader);

    var methodB = methodB(methodTB(stringTB(), list()), jar, stringB(classBinaryName));
    var resultMethod1 = nativeMethodLoader.load("smoothName", methodB);
    var resultMethod2 = nativeMethodLoader.load("smoothName", methodB);
    assertThat(resultMethod1)
        .isEqualTo(expected);
    assertThat(resultMethod1)
        .isSameInstanceAs(resultMethod2);
    verify(methodLoader, times(1))
        .provide(methodSpec);
  }
}
