package org.smoothbuild.virtualmachine.bytecode.expr;

import java.io.IOException;
import java.util.function.Function;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.BKindDbException;

public class Helpers {
  public static <R, T extends Throwable> R invokeAndChainIOException(
      Function0<R, IOException> function0, Function<IOException, T> exceptionWrapper) throws T {
    try {
      return function0.apply();
    } catch (IOException e) {
      throw exceptionWrapper.apply(e);
    }
  }

  public static <R, T extends Throwable> R invokeAndChainBytecodeException(
      Function0<R, BytecodeException> function0, Function<BytecodeException, T> exceptionWrapper)
      throws T {
    try {
      return function0.apply();
    } catch (BytecodeException e) {
      throw exceptionWrapper.apply(e);
    }
  }

  public static <R, T extends Throwable> R invokeAndChainKindDbException(
      Function0<R, BKindDbException> function0, Function<BKindDbException, T> exceptionWrapper)
      throws T {
    try {
      return function0.apply();
    } catch (BKindDbException e) {
      throw exceptionWrapper.apply(e);
    }
  }

  public static <R, T extends Throwable> R invokeAndChainHashedDbException(
      Function0<R, HashedDbException> function0, Function<HashedDbException, T> exceptionWrapper)
      throws T {
    try {
      return function0.apply();
    } catch (HashedDbException e) {
      throw exceptionWrapper.apply(e);
    }
  }
}
