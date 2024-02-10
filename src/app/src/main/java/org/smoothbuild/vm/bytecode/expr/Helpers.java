package org.smoothbuild.vm.bytecode.expr;

import org.smoothbuild.common.function.Function0;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.exc.BytecodeDbException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.vm.bytecode.type.CategoryB;

public class Helpers {
  public static <T> T wrapHashedDbExcAsBytecodeDbExc(Function0<T, HashedDbException> function0)
      throws BytecodeDbException {
    try {
      return function0.apply();
    } catch (HashedDbException e) {
      throw new BytecodeDbException(e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeExprNodeException(
      Hash hash, CategoryB cat, String path, Function0<T, HashedDbException> function0)
      throws DecodeExprNodeException {
    try {
      return function0.apply();
    } catch (HashedDbException e) {
      throw new DecodeExprNodeException(hash, cat, path, e);
    }
  }

  public static <T> T wrapBytecodeExcAsDecodeExprNodeException(
      Hash hash, CategoryB cat, String path, Function0<T, BytecodeException> callable)
      throws DecodeExprNodeException {
    try {
      return callable.apply();
    } catch (BytecodeException e) {
      throw new DecodeExprNodeException(hash, cat, path, e);
    }
  }
}
