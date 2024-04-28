package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getSystemClassLoader;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;
import org.smoothbuild.virtualmachine.testing.func.nativ.NonPublicMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.NonStaticMethod;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.nativ.TooFewParameters;
import org.smoothbuild.virtualmachine.testing.func.nativ.TooManyParameters;
import org.smoothbuild.virtualmachine.testing.func.nativ.WrongParameterType;
import org.smoothbuild.virtualmachine.testing.func.nativ.WrongReturnType;

public class NativeMethodLoaderTest extends TestingVirtualMachine {
  @Test
  void non_public_method_causes_error() throws Exception {
    assertLoadingCausesError(NonPublicMethod.class, "Providing method is not public.");
  }

  @Test
  void non_static_method_causes_error() throws Exception {
    assertLoadingCausesError(NonStaticMethod.class, "Providing method is not static.");
  }

  @Test
  void wrong_return_type_in_method_causes_error() throws Exception {
    assertLoadingCausesError(WrongReturnType.class, wrongReturnTypeErrorMessage());
  }

  @Test
  void too_few_parameters_in_method_causes_error() throws Exception {
    assertLoadingCausesError(TooFewParameters.class, wrongParametersErrorMessage());
  }

  @Test
  void too_many_parameters_in_method_causes_error() throws Exception {
    assertLoadingCausesError(TooManyParameters.class, wrongParametersErrorMessage());
  }

  @Test
  void wrong_parameter_type_in_method_causes_error() throws Exception {
    assertLoadingCausesError(WrongParameterType.class, wrongParametersErrorMessage());
  }

  private void assertLoadingCausesError(Class<?> clazz, String message) throws BytecodeException {
    var nativeMethodLoader = nativeMethodLoaderWithPlatformClassLoader();
    assertThat(nativeMethodLoader.load(bMethod(clazz))).isEqualTo(loadingError(clazz, message));
  }

  private String wrongReturnTypeErrorMessage() {
    return "Providing method should declare return type as " + BValue.class.getCanonicalName()
        + " but is " + BOrder.class.getCanonicalName() + ".";
  }

  private String wrongParametersErrorMessage() {
    return "Providing method should have two parameters " + NativeApi.class.getCanonicalName()
        + " and " + BTuple.class.getCanonicalName() + ".";
  }

  private NativeMethodLoader nativeMethodLoaderWithPlatformClassLoader() {
    return new NativeMethodLoader(
        new MethodLoader(new JarClassLoaderFactory(bytecodeF(), getSystemClassLoader())));
  }

  private Either<String, Object> loadingError(Class<?> clazz, String message) {
    return left("Error loading native implementation specified as `" + clazz.getCanonicalName()
        + "`: " + message);
  }

  @Nested
  class _caching {
    @Test
    void method_is_cached() throws Exception {
      var method = ReturnAbc.class.getDeclaredMethod(
          NativeMethodLoader.NATIVE_METHOD_NAME, NativeApi.class, BTuple.class);
      testCaching(method, right(method), right(method));
    }

    @Test
    void error_when_loading_method_is_cached() throws Exception {
      var method = NonPublicMethod.class.getDeclaredMethod(
          NativeMethodLoader.NATIVE_METHOD_NAME, NativeApi.class, BTuple.class);
      testCaching(
          method,
          left("xx"),
          left("Error loading native implementation specified as `binary.name`: xx"));
    }

    private void testCaching(
        Method method, Either<String, Method> eitherMethod, Either<String, Method> expected)
        throws Exception {
      var methodLoader = mock(MethodLoader.class);
      var jar = bBlob();
      var classBinaryName = "binary.name";
      var bMethod = bMethod(jar, classBinaryName, method.getName());
      when(methodLoader.load(bMethod)).thenReturn(eitherMethod);

      var nativeMethodLoader = new NativeMethodLoader(methodLoader);

      var resultMethod1 = nativeMethodLoader.load(bMethod);
      var resultMethod2 = nativeMethodLoader.load(bMethod);
      assertThat(resultMethod1).isEqualTo(expected);
      assertThat(resultMethod1).isSameInstanceAs(resultMethod2);
      verify(methodLoader, times(1)).load(bMethod);
    }
  }
}
