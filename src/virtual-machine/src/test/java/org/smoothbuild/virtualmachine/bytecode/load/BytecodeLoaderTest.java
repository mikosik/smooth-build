package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.collect.Map.map;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ThrowException;

public class BytecodeLoaderTest extends BytecodeTestContext {
  @Test
  void loading_bytecode() throws Exception {
    assertThat(loadBytecode(ReturnAbc.class, map())).isEqualTo(right(bString("abc")));
  }

  @Test
  void loading_monomorphised_bytecode() throws Exception {
    assertThat(loadBytecode(ReturnIdFunc.class, map("A", bIntType())))
        .isEqualTo(right(bIntIdLambda()));
  }

  @Test
  void loading_bytecode_exception_is_returned_as_error() throws Exception {
    assertThat(loadBytecode(ThrowException.class, map()))
        .isEqualTo(loadingError("Providing method thrown exception: java.lang"
            + ".UnsupportedOperationException: detailed message"));
  }

  private Either<String, BExpr> loadBytecode(Class<?> clazz, Map<String, BType> varMap)
      throws NoSuchMethodException, BytecodeException {
    var jar = bBlob();
    var classBinaryName = "binary.name";
    var bMethod = bMethod(jar, classBinaryName);
    var bytecodeMethodLoader = mock(BytecodeMethodLoader.class);
    when(bytecodeMethodLoader.load(bMethod)).thenReturn(fetchMethod(clazz));

    return new BytecodeLoader(bytecodeMethodLoader, bytecodeF()).load("name", bMethod, varMap);
  }

  private static Either<String, Method> fetchMethod(Class<?> clazz) throws NoSuchMethodException {
    return right(clazz.getDeclaredMethod(
        BytecodeMethodLoader.BYTECODE_METHOD_NAME, BytecodeFactory.class, java.util.Map.class));
  }

  private Either<String, Object> loadingError(String message) {
    return left(
        "Error loading bytecode for `name` using provider specified as `binary.name`: " + message);
  }
}
