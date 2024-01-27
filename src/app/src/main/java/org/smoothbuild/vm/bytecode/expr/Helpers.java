package org.smoothbuild.vm.bytecode.expr;

import org.smoothbuild.common.function.Consumer0;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.vm.bytecode.expr.exc.BytecodeDbException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.vm.bytecode.type.CategoryB;

public class Helpers {
  public static void wrapHashedDbExcAsBytecodeDbExc(Consumer0<HashedDbException> runnable) {
    try {
      runnable.accept();
    } catch (HashedDbException e) {
      throw new BytecodeDbException(e);
    }
  }

  public static <T> T wrapHashedDbExcAsBytecodeDbExc(Function0<T, HashedDbException> function0) {
    try {
      return function0.apply();
    } catch (HashedDbException e) {
      throw new BytecodeDbException(e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeExprNodeException(
      Hash hash, CategoryB cat, String path, Function0<T, HashedDbException> function0) {
    try {
      return function0.apply();
    } catch (HashedDbException e) {
      throw new DecodeExprNodeException(hash, cat, path, e);
    }
  }

  public static <T> T wrapBytecodeDbExcAsDecodeExprNodeException(
      Hash hash, CategoryB cat, String path, Function0<T, BytecodeDbException> callable) {
    try {
      return callable.apply();
    } catch (BytecodeDbException e) {
      throw new DecodeExprNodeException(hash, cat, path, e);
    }
  }

  public static <T> T wrapBytecodeDbExcAsDecodeExprNodeException(
      Hash hash,
      CategoryB cat,
      String path,
      int pathIndex,
      Function0<T, BytecodeDbException> callable) {
    try {
      return callable.apply();
    } catch (BytecodeDbException e) {
      throw new DecodeExprNodeException(hash, cat, path + "[" + pathIndex + "]", e);
    }
  }
}
