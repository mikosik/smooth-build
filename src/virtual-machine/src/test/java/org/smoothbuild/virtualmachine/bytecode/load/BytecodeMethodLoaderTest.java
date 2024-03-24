package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;

import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;
import org.smoothbuild.virtualmachine.testing.func.bytecode.NonPublicMethod;
import org.smoothbuild.virtualmachine.testing.func.bytecode.NonStaticMethod;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.WithNonValueResult;
import org.smoothbuild.virtualmachine.testing.func.bytecode.WithThreeParams;
import org.smoothbuild.virtualmachine.testing.func.bytecode.WithoutBytecodeF;

public class BytecodeMethodLoaderTest extends TestingVirtualMachine {
  @Nested
  class _caching {
    @Test
    public void method_is_cached() throws Exception {
      var method = fetchJMethod(ReturnAbc.class);
      testCaching(method, right(method), right(method));
    }

    @Test
    public void error_when_loading_method_is_cached() throws Exception {
      var method = fetchJMethod(NonPublicMethod.class);
      testCaching(method, left("error message"), left("error message"));
    }

    private void testCaching(
        Method method, Either<String, Method> eitherMethod, Either<String, Method> expected)
        throws BytecodeException {
      var methodLoader = mock(MethodLoader.class);
      var jar = bBlob();
      var classBinaryName = "binary.name";
      var methodSpec = new MethodSpec(jar, classBinaryName, method.getName());
      when(methodLoader.load(methodSpec)).thenReturn(eitherMethod);

      var bytecodeMethodLoader = new BytecodeMethodLoader(methodLoader);

      var resultMethod1 = bytecodeMethodLoader.load(jar, classBinaryName);
      var resultMethod2 = bytecodeMethodLoader.load(jar, classBinaryName);
      assertThat(resultMethod1).isEqualTo(expected);
      assertThat(resultMethod1).isSameInstanceAs(resultMethod2);
      verify(methodLoader, times(1)).load(methodSpec);
    }
  }

  @Test
  public void loading_non_public_method_causes_error() throws Exception {
    assertLoadingCausesError(NonPublicMethod.class, "Providing method is not public.");
  }

  @Test
  public void loading_non_static_method_causes_error() throws Exception {
    assertLoadingCausesError(NonStaticMethod.class, "Providing method is not static.");
  }

  @Test
  public void loading_method_without_native_api_param_causes_error() throws Exception {
    var method =
        WithoutBytecodeF.class.getDeclaredMethod(BytecodeMethodLoader.BYTECODE_METHOD_NAME);
    assertLoadingCausesError(
        method,
        "Providing method parameter is not of type " + BytecodeFactory.class.getCanonicalName()
            + ".");
  }

  @Test
  public void loading_method_with_three_params_causes_error() throws Exception {
    var method = WithThreeParams.class.getDeclaredMethod(
        BytecodeMethodLoader.BYTECODE_METHOD_NAME, BytecodeFactory.class, Map.class, Map.class);
    assertLoadingCausesError(method, "Providing method parameter count is different than 2.");
  }

  @Test
  public void loading_method_with_non_val_result_causes_error() throws Exception {
    assertLoadingCausesError(
        WithNonValueResult.class,
        "Providing method result type is not " + BValue.class.getCanonicalName() + ".");
  }

  private void assertLoadingCausesError(Class<?> clazz, String message) throws Exception {
    assertLoadingCausesError(fetchJMethod(clazz), message);
  }

  private void assertLoadingCausesError(Method method, String message) throws BytecodeException {
    var methodSpec =
        new MethodSpec(bBlob(), "class.binary.name", BytecodeMethodLoader.BYTECODE_METHOD_NAME);
    assertThat(load(methodSpec, method)).isEqualTo(left(message));
  }

  private Either<String, Method> load(MethodSpec methodSpec, Method method)
      throws BytecodeException {
    var methodLoader = mock(MethodLoader.class);
    doReturn(right(method)).when(methodLoader).load(methodSpec);
    var bytecodeMethodLoader = new BytecodeMethodLoader(methodLoader);
    return bytecodeMethodLoader.load(methodSpec.jar(), methodSpec.classBinaryName());
  }

  private static Method fetchJMethod(Class<?> clazz) throws NoSuchMethodException {
    return clazz.getDeclaredMethod(
        BytecodeMethodLoader.BYTECODE_METHOD_NAME, BytecodeFactory.class, Map.class);
  }
}
