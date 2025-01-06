package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.testing.VmTestContext;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ThrowException;

public class BytecodeLoaderTest extends VmTestContext {
  @Test
  void loading_bytecode() throws Exception {
    assertThat(loadBytecode(ReturnAbc.class, map())).isEqualTo(ok(bString("abc")));
  }

  @Test
  void loading_monomorphised_bytecode() throws Exception {
    assertThat(loadBytecode(ReturnIdFunc.class, map("A", bIntType())))
        .isEqualTo(ok(bIntIdLambda()));
  }

  @Test
  void loading_bytecode_exception_is_returned_as_error() throws Exception {
    assertThat(loadBytecode(ThrowException.class, map()))
        .isEqualTo(loadingError("Providing method thrown exception: java.lang"
            + ".UnsupportedOperationException: detailed message"));
  }

  private Result<BExpr> loadBytecode(Class<?> clazz, Map<String, BType> varMap) throws Exception {
    var jar = bBlob();
    var classBinaryName = "binary.name";
    var bMethod = bMethod(jar, classBinaryName);
    var bytecodeMethodLoader = mock(BytecodeMethodLoader.class);
    when(bytecodeMethodLoader.load(bMethod)).thenReturn(fetchMethod(clazz));

    return new BytecodeLoader(bytecodeMethodLoader, bytecodeF()).load("name", bMethod, varMap);
  }

  private static Result<Method> fetchMethod(Class<?> clazz) throws NoSuchMethodException {
    return ok(clazz.getDeclaredMethod(
        BytecodeMethodLoader.BYTECODE_METHOD_NAME, BytecodeFactory.class, java.util.Map.class));
  }

  private Result<Object> loadingError(String message) {
    return err(
        "Error loading bytecode for `name` using provider specified as `binary.name`: " + message);
  }
}
