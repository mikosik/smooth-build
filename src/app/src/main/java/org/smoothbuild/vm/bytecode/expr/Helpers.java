package org.smoothbuild.vm.bytecode.expr;

import java.util.function.Function;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.vm.bytecode.type.exc.CategoryDbException;

public class Helpers {
  public static <R, T extends Throwable> R invokeTranslatingBytecodeException(
      Function0<R, BytecodeException> function0, Function<BytecodeException, T> exceptionTranslator)
      throws T {
    try {
      return function0.apply();
    } catch (BytecodeException e) {
      throw exceptionTranslator.apply(e);
    }
  }

  public static <R, T extends Throwable> R invokeTranslatingCategoryDbException(
      Function0<R, CategoryDbException> function0,
      Function<CategoryDbException, T> exceptionTranslator)
      throws T {
    try {
      return function0.apply();
    } catch (CategoryDbException e) {
      throw exceptionTranslator.apply(e);
    }
  }

  public static <R, T extends Throwable> R invokeTranslatingHashedDbException(
      Function0<R, HashedDbException> function0, Function<HashedDbException, T> exceptionTranslator)
      throws T {
    try {
      return function0.apply();
    } catch (HashedDbException e) {
      throw exceptionTranslator.apply(e);
    }
  }
}
