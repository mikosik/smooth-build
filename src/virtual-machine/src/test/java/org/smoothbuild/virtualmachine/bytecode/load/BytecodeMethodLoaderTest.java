package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getSystemClassLoader;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.virtualmachine.bytecode.load.BytecodeMethodLoader.BYTECODE_METHOD_NAME;

import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;
import org.smoothbuild.virtualmachine.testing.func.bytecode.NonPublicMethod;
import org.smoothbuild.virtualmachine.testing.func.bytecode.NonStaticMethod;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.WithNonValueResult;
import org.smoothbuild.virtualmachine.testing.func.bytecode.WithThreeParams;
import org.smoothbuild.virtualmachine.testing.func.bytecode.WithoutBytecodeF;

public class BytecodeMethodLoaderTest extends VmTestContext {
  @Nested
  class _caching {
    @Test
    void method_is_cached() throws Exception {
      var method = fetchJMethod(ReturnAbc.class);
      testCaching(ok(method), ok(method));
    }

    @Test
    void error_when_loading_method_is_cached() throws Exception {
      var method = fetchJMethod(NonPublicMethod.class);
      testCaching(err("error message"), err("error message"));
    }

    private void testCaching(Result<Method> resultMethod, Result<Method> expected)
        throws Exception {
      var methodLoader = mock(MethodLoader.class);
      var jar = bBlob();
      var classBinaryName = "binary.name";
      var bMethod = bMethod(jar, classBinaryName, BYTECODE_METHOD_NAME);
      when(methodLoader.load(bMethod)).thenReturn(resultMethod);

      var bytecodeMethodLoader = new BytecodeMethodLoader(methodLoader);

      var resultMethod1 = bytecodeMethodLoader.load(bMethod);
      var resultMethod2 = bytecodeMethodLoader.load(bMethod);
      assertThat(resultMethod1).isEqualTo(expected);
      assertThat(resultMethod1).isSameInstanceAs(resultMethod2);
      verify(methodLoader, times(1)).load(bMethod);
    }
  }

  @Test
  void loading_non_public_method_causes_error() throws Exception {
    assertLoadingCausesError(NonPublicMethod.class, "Providing method is not public.");
  }

  @Test
  void loading_non_static_method_causes_error() throws Exception {
    assertLoadingCausesError(NonStaticMethod.class, "Providing method is not static.");
  }

  @Test
  void loading_method_without_native_api_param_causes_error() throws Exception {
    assertLoadingCausesError(
        WithoutBytecodeF.class,
        "Providing method parameter is not of type " + BytecodeFactory.class.getCanonicalName()
            + ".");
  }

  @Test
  void loading_method_with_three_params_causes_error() throws Exception {
    assertLoadingCausesError(
        WithThreeParams.class, "Providing method parameter count is different than 2.");
  }

  @Test
  void loading_method_with_non_val_result_causes_error() throws Exception {
    assertLoadingCausesError(
        WithNonValueResult.class,
        "Providing method result type is not " + BValue.class.getCanonicalName() + ".");
  }

  private void assertLoadingCausesError(Class<?> clazz, String message) throws Exception {
    var bMethod = bMethod(clazz, BYTECODE_METHOD_NAME);
    var bytecodeMethodLoader = bytecodeMethodLoader(getSystemClassLoader());
    assertThat(bytecodeMethodLoader.load(bMethod)).isEqualTo(err(message));
  }

  private static Method fetchJMethod(Class<?> clazz) throws NoSuchMethodException {
    return clazz.getDeclaredMethod(BYTECODE_METHOD_NAME, BytecodeFactory.class, Map.class);
  }
}
