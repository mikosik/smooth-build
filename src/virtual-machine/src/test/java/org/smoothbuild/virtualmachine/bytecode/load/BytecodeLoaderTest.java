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
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ThrowException;

public class BytecodeLoaderTest extends TestingVirtualMachine {
  @Test
  public void loading_bytecode() throws Exception {
    assertThat(loadBytecode(ReturnAbc.class, map())).isEqualTo(right(stringB("abc")));
  }

  @Test
  public void loading_monomorphised_bytecode() throws Exception {
    assertThat(loadBytecode(ReturnIdFunc.class, map("A", intTB()))).isEqualTo(right(idFuncB()));
  }

  @Test
  public void loading_bytecode_exception_is_returned_as_error() throws Exception {
    assertThat(loadBytecode(ThrowException.class, map()))
        .isEqualTo(loadingError("Providing method thrown exception: java.lang"
            + ".UnsupportedOperationException: detailed message"));
  }

  private Either<String, ExprB> loadBytecode(Class<?> clazz, Map<String, TypeB> varMap)
      throws NoSuchMethodException, BytecodeException {
    var jar = blobB();
    var classBinaryName = "binary.name";
    var bytecodeMethodLoader = mock(BytecodeMethodLoader.class);
    when(bytecodeMethodLoader.load(jar, classBinaryName)).thenReturn(fetchMethod(clazz));

    return new BytecodeLoader(bytecodeMethodLoader, bytecodeF())
        .load("name", jar, classBinaryName, varMap);
  }

  private static Either<String, Method> fetchMethod(Class<?> clazz) throws NoSuchMethodException {
    return right(clazz.getDeclaredMethod(
        BytecodeMethodLoader.BYTECODE_METHOD_NAME, BytecodeF.class, java.util.Map.class));
  }

  private Either<String, Object> loadingError(String message) {
    return left(
        "Error loading bytecode for `name` using provider specified as `binary.name`: " + message);
  }
}
