package org.smoothbuild.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.compile.BytecodeMethodLoader.BYTECODE_METHOD_NAME;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.load.MethodLoader;
import org.smoothbuild.load.MethodSpec;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.func.bytecode.NonPublicMethod;
import org.smoothbuild.testing.func.bytecode.NonStaticMethod;
import org.smoothbuild.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.testing.func.bytecode.WithNonObjRes;
import org.smoothbuild.testing.func.bytecode.WithTwoParams;
import org.smoothbuild.testing.func.bytecode.WithoutBytecodeF;
import org.smoothbuild.util.collect.Result;

public class BytecodeMethodLoaderTest extends TestingContext {
  @Nested
  class _caching {
    @Test
    public void method_is_cached() throws Exception {
      var method = fetchJMethod(ReturnAbc.class);
      testCaching(method, Result.of(method), Result.of(method));
    }

    @Test
    public void error_when_loading_method_is_cached() throws Exception {
      var method = fetchJMethod(NonPublicMethod.class);
      testCaching(method, Result.error("error message"), Result.error("error message"));
    }

    private void testCaching(Method method, Result<Method> resultMethod, Result<Method> expected) {
      var methodProv = mock(MethodLoader.class);
      BlobB jar = blobB();
      String classBinaryName = "binary.name";
      var methodSpec = new MethodSpec(jar, classBinaryName, method.getName());
      when(methodProv.provide(methodSpec))
          .thenReturn(resultMethod);

      var methodLoader = new BytecodeMethodLoader(methodProv);

      var resultMethod1 = methodLoader.load(jar, classBinaryName);
      var resultMethod2 = methodLoader.load(jar, classBinaryName);
      assertThat(resultMethod1)
          .isEqualTo(expected);
      assertThat(resultMethod1)
          .isSameInstanceAs(resultMethod2);
      verify(methodProv, times(1))
          .provide(methodSpec);
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
    var method = WithoutBytecodeF.class.getDeclaredMethod(BYTECODE_METHOD_NAME);
    assertLoadingCausesError(method,
        "Providing method parameter is not of type " + BytecodeF.class.getCanonicalName() + ".");
  }

  @Test
  public void loading_method_with_two_params_causes_error() throws Exception {
    var method = WithTwoParams.class.getDeclaredMethod(BYTECODE_METHOD_NAME,
        BytecodeF.class, BytecodeF.class);
    assertLoadingCausesError(method, "Providing method has more than one parameter.");
  }

  @Test
  public void loading_method_with_non_objb_result_causes_error() throws Exception {
    assertLoadingCausesError(WithNonObjRes.class,
        "Providing method result type is not org.smoothbuild.bytecode.obj.base.ObjB.");
  }

  private void assertLoadingCausesError(Class<?> clazz, String message) throws Exception {
    assertLoadingCausesError(fetchJMethod(clazz), message);
  }

  private void assertLoadingCausesError(Method method, String message) {
    var methodSpec = new MethodSpec(blobB(), "class.binary.name", BYTECODE_METHOD_NAME);
    assertThat(load(methodSpec, method))
        .isEqualTo(Result.error(message));
  }

  private Result<Method> load(MethodSpec methodSpec, Method method) {
    var methodLoader = mock(MethodLoader.class);
    doReturn(Result.of(method))
        .when(methodLoader)
        .provide(methodSpec);
    var bytecodeMethodLoader = new BytecodeMethodLoader(methodLoader);
    return bytecodeMethodLoader.load(methodSpec.jar(), methodSpec.classBinaryName());
  }

  private static Method fetchJMethod(Class<?> clazz) throws NoSuchMethodException {
    return clazz.getDeclaredMethod(BYTECODE_METHOD_NAME, BytecodeF.class);
  }
}