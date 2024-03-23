package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getSystemClassLoader;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;

import java.io.IOException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
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
  public void non_public_method_causes_error() throws Exception {
    assertLoadingCausesError(NonPublicMethod.class, "Providing method is not public.");
  }

  @Test
  public void non_static_method_causes_error() throws Exception {
    assertLoadingCausesError(NonStaticMethod.class, "Providing method is not static.");
  }

  @Test
  public void wrong_return_type_in_method_causes_error() throws Exception {
    assertLoadingCausesError(WrongReturnType.class, wrongReturnTypeErrorMessage());
  }

  @Test
  public void too_few_parameters_in_method_causes_error() throws Exception {
    assertLoadingCausesError(TooFewParameters.class, wrongParametersErrorMessage());
  }

  @Test
  public void too_many_parameters_in_method_causes_error() throws Exception {
    assertLoadingCausesError(TooManyParameters.class, wrongParametersErrorMessage());
  }

  @Test
  public void wrong_parameter_type_in_method_causes_error() throws Exception {
    assertLoadingCausesError(WrongParameterType.class, wrongParametersErrorMessage());
  }

  private void assertLoadingCausesError(Class<?> clazz, String message)
      throws IOException, BytecodeException {
    var nativeMethodLoader = nativeMethodLoaderWithPlatformClassLoader();
    assertThat(nativeMethodLoader.load(nativeFuncB(clazz))).isEqualTo(loadingError(clazz, message));
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
    public void method_is_cached() throws Exception {
      var method = ReturnAbc.class.getDeclaredMethod(
          NativeMethodLoader.NATIVE_METHOD_NAME, NativeApi.class, BTuple.class);
      testCaching(method, right(method), right(method));
    }

    @Test
    public void error_when_loading_method_is_cached() throws Exception {
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
      var jar = blobB();
      var classBinaryName = "binary.name";
      var methodSpec = new MethodSpec(jar, classBinaryName, method.getName());
      when(methodLoader.load(methodSpec)).thenReturn(eitherMethod);

      var nativeMethodLoader = new NativeMethodLoader(methodLoader);

      var nativeFunc = nativeFuncB(funcTB(stringTB()), jar, stringB(classBinaryName));
      var resultMethod1 = nativeMethodLoader.load(nativeFunc);
      var resultMethod2 = nativeMethodLoader.load(nativeFunc);
      assertThat(resultMethod1).isEqualTo(expected);
      assertThat(resultMethod1).isSameInstanceAs(resultMethod2);
      verify(methodLoader, times(1)).load(methodSpec);
    }
  }
}
