package org.smoothbuild.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.compile.BytecodeMethodLoader.BYTECODE_METHOD_NAME;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.testing.func.bytecode.ThrowException;
import org.smoothbuild.util.collect.Result;

public class BytecodeLoaderTest extends TestingContext {
  @Test
  public void loading_bytecode() throws Exception {
    assertThat(loadBytecode(ReturnAbc.class))
        .isEqualTo(Result.of(stringB("abc")));
  }

  @Test
  public void loading_bytecode_exception_is_returned_as_error() throws Exception {
    assertThat(loadBytecode(ThrowException.class))
        .isEqualTo(loadingError("Providing method thrown exception: java.lang"
            + ".UnsupportedOperationException: detailed message"));
  }

  private Result<ObjB> loadBytecode(Class<?> clazz) throws NoSuchMethodException {
    var name = "name";
    var jar = blobB();
    var classBinaryName = "binary.name";
    var bytecodeMethodLoader = mock(BytecodeMethodLoader.class);
    when(bytecodeMethodLoader.load(name, jar, classBinaryName))
        .thenReturn(fetchMethod(clazz));

    return new BytecodeLoader(bytecodeMethodLoader, bytecodeF())
        .load(name, jar, classBinaryName);
  }

  private static Result<Method> fetchMethod(Class<?> clazz) throws NoSuchMethodException {
    return Result.of(clazz.getDeclaredMethod(BYTECODE_METHOD_NAME, BytecodeF.class));
  }

  private Result<Object> loadingError(String message) {
    return Result.error(
        "Error loading bytecode for `name` using provider specified as `binary.name`: " + message);
  }
}
