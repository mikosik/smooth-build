package org.smoothbuild.compile.sb;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.testing.func.bytecode.ThrowException;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import com.google.common.collect.ImmutableMap;

public class BytecodeLoaderTest extends TestContext {
  @Test
  public void loading_bytecode() throws Exception {
    assertThat(loadBytecode(ReturnAbc.class, new HashMap<>()))
        .isEqualTo(Try.result(stringB("abc")));
  }

  @Test
  public void loading_monomorphised_bytecode() throws Exception {
    assertThat(loadBytecode(ReturnIdFunc.class, ImmutableMap.of("A", intTB())))
        .isEqualTo(Try.result(idFuncB()));
  }

  @Test
  public void loading_bytecode_exception_is_returned_as_error() throws Exception {
    assertThat(loadBytecode(ThrowException.class, new HashMap<>()))
        .isEqualTo(loadingError("Providing method thrown exception: java.lang"
            + ".UnsupportedOperationException: detailed message"));
  }

  private Try<ExprB> loadBytecode(Class<?> clazz, Map<String, TypeB> varMap)
      throws NoSuchMethodException {
    var jar = blobB();
    var classBinaryName = "binary.name";
    var bytecodeMethodLoader = mock(BytecodeMethodLoader.class);
    when(bytecodeMethodLoader.load(jar, classBinaryName))
        .thenReturn(fetchMethod(clazz));

    return new BytecodeLoader(bytecodeMethodLoader, bytecodeF())
        .load("name", jar, classBinaryName, varMap);
  }

  private static Try<Method> fetchMethod(Class<?> clazz) throws NoSuchMethodException {
    return Try.result(clazz.getDeclaredMethod(BytecodeMethodLoader.BYTECODE_METHOD_NAME, BytecodeF.class, Map.class));
  }

  private Try<Object> loadingError(String message) {
    return Try.error(
        "Error loading bytecode for `name` using provider specified as `binary.name`: " + message);
  }
}
