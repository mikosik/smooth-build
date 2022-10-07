package org.smoothbuild.vm.task;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getSystemClassLoader;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.load.JarClassLoaderProv;
import org.smoothbuild.load.MethodLoader;
import org.smoothbuild.load.MethodSpec;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.nativ.NonPublicMethod;
import org.smoothbuild.testing.func.nativ.NonStaticMethod;
import org.smoothbuild.testing.func.nativ.ReturnAbc;
import org.smoothbuild.testing.func.nativ.TooFewParameters;
import org.smoothbuild.testing.func.nativ.TooManyParameters;
import org.smoothbuild.testing.func.nativ.WrongParameterType;
import org.smoothbuild.testing.func.nativ.WrongReturnType;
import org.smoothbuild.util.collect.Try;

public class NativeMethodLoaderTest extends TestContext {
  @Test
  public void non_public_method_causes_error() throws IOException {
    assertLoadingCausesError(NonPublicMethod.class, "Providing method is not public.");
  }

  @Test
  public void non_static_method_causes_error() throws IOException {
    assertLoadingCausesError(NonStaticMethod.class, "Providing method is not static.");
  }

  @Test
  public void wrong_return_type_in_method_causes_error() throws IOException {
    assertLoadingCausesError(WrongReturnType.class, wrongReturnTypeErrorMessage());
  }

  @Test
  public void too_few_parameters_in_method_causes_error() throws IOException {
    assertLoadingCausesError(TooFewParameters.class, wrongParametersErrorMessage());
  }

  @Test
  public void too_many_parameters_in_method_causes_error() throws IOException {
    assertLoadingCausesError(TooManyParameters.class, wrongParametersErrorMessage());
  }

  @Test
  public void wrong_parameter_type_in_method_causes_error() throws IOException {
    assertLoadingCausesError(WrongParameterType.class, wrongParametersErrorMessage());
  }

  private void assertLoadingCausesError(Class<?> clazz, String message) throws IOException {
    var nativeMethodLoader = nativeMethodLoaderWithPlatformClassLoader();
    assertThat(nativeMethodLoader.load("name", natFuncB(clazz)))
        .isEqualTo(loadingError("name", clazz, message));
  }

  private String wrongReturnTypeErrorMessage() {
    return "Providing method should declare return type as " + InstB.class.getCanonicalName()
        + " but is " + OrderB.class.getCanonicalName() + ".";
  }

  private String wrongParametersErrorMessage() {
    return "Providing method should have two parameters " + NativeApi.class.getCanonicalName()
        + " and " + TupleB.class.getCanonicalName() + ".";
  }

  private NativeMethodLoader nativeMethodLoaderWithPlatformClassLoader() {
    return new NativeMethodLoader(new MethodLoader(
        new JarClassLoaderProv(bytecodeF(), getSystemClassLoader())));
  }

  private Try<Object> loadingError(String name, Class<?> clazz, String message) {
    return Try.error("Error loading native implementation for `" + name + "` specified as `"
        + clazz.getCanonicalName() + "`: " + message);
  }

  @Nested
  class _caching {
    @Test
    public void method_is_cached() throws Exception {
      var method = ReturnAbc.class.getDeclaredMethod(
          NativeMethodLoader.NATIVE_METHOD_NAME, NativeApi.class, TupleB.class);
      testCaching(method, Try.result(method), Try.result(method));
    }

    @Test
    public void error_when_loading_method_is_cached() throws Exception {
      var method = NonPublicMethod.class.getDeclaredMethod(
          NativeMethodLoader.NATIVE_METHOD_NAME, NativeApi.class, TupleB.class);
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

      var natFuncB = natFuncB(funcTB(stringTB()), jar, stringB(classBinaryName));
      var resultMethod1 = nativeMethodLoader.load("smoothName", natFuncB);
      var resultMethod2 = nativeMethodLoader.load("smoothName", natFuncB);
      assertThat(resultMethod1)
          .isEqualTo(expected);
      assertThat(resultMethod1)
          .isSameInstanceAs(resultMethod2);
      verify(methodLoader, times(1))
          .provide(methodSpec);
    }
  }
}
