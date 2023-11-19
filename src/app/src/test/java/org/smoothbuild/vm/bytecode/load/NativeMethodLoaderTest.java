package org.smoothbuild.vm.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getSystemClassLoader;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.vavr.control.Either;
import java.io.IOException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.nativ.NonPublicMethod;
import org.smoothbuild.testing.func.nativ.NonStaticMethod;
import org.smoothbuild.testing.func.nativ.ReturnAbc;
import org.smoothbuild.testing.func.nativ.TooFewParameters;
import org.smoothbuild.testing.func.nativ.TooManyParameters;
import org.smoothbuild.testing.func.nativ.WrongParameterType;
import org.smoothbuild.testing.func.nativ.WrongReturnType;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

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
    assertThat(nativeMethodLoader.load(nativeFuncB(clazz))).isEqualTo(loadingError(clazz, message));
  }

  private String wrongReturnTypeErrorMessage() {
    return "Providing method should declare return type as " + ValueB.class.getCanonicalName()
        + " but is " + OrderB.class.getCanonicalName() + ".";
  }

  private String wrongParametersErrorMessage() {
    return "Providing method should have two parameters " + NativeApi.class.getCanonicalName()
        + " and " + TupleB.class.getCanonicalName() + ".";
  }

  private NativeMethodLoader nativeMethodLoaderWithPlatformClassLoader() {
    return new NativeMethodLoader(
        new MethodLoader(new JarClassLoaderProv(bytecodeF(), getSystemClassLoader())));
  }

  private Either<String, Object> loadingError(Class<?> clazz, String message) {
    return Either.left("Error loading native implementation specified as `"
        + clazz.getCanonicalName() + "`: " + message);
  }

  @Nested
  class _caching {
    @Test
    public void method_is_cached() throws Exception {
      var method = ReturnAbc.class.getDeclaredMethod(
          NativeMethodLoader.NATIVE_METHOD_NAME, NativeApi.class, TupleB.class);
      testCaching(method, Either.right(method), Either.right(method));
    }

    @Test
    public void error_when_loading_method_is_cached() throws Exception {
      var method = NonPublicMethod.class.getDeclaredMethod(
          NativeMethodLoader.NATIVE_METHOD_NAME, NativeApi.class, TupleB.class);
      testCaching(
          method,
          Either.left("xx"),
          Either.left("Error loading native implementation specified as `binary.name`: xx"));
    }

    private void testCaching(
        Method method, Either<String, Method> eitherMethod, Either<String, Method> expected) {
      var methodLoader = mock(MethodLoader.class);
      var jar = blobB();
      var classBinaryName = "binary.name";
      var methodSpec = new MethodSpec(jar, classBinaryName, method.getName());
      when(methodLoader.provide(methodSpec)).thenReturn(eitherMethod);

      var nativeMethodLoader = new NativeMethodLoader(methodLoader);

      var nativeFuncB = nativeFuncB(funcTB(stringTB()), jar, stringB(classBinaryName));
      var resultMethod1 = nativeMethodLoader.load(nativeFuncB);
      var resultMethod2 = nativeMethodLoader.load(nativeFuncB);
      assertThat(resultMethod1).isEqualTo(expected);
      assertThat(resultMethod1).isSameInstanceAs(resultMethod2);
      verify(methodLoader, times(1)).provide(methodSpec);
    }
  }
}
