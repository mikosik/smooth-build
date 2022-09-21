package org.smoothbuild.bytecode.expr;

import org.smoothbuild.bytecode.expr.exc.BytecodeDbExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprNodeExc;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.bytecode.type.CategoryB;

public class Helpers {
  public static void wrapHashedDbExcAsBytecodeDbExc(HashedDbRunnable runnable) {
    try {
      runnable.run();
    } catch (HashedDbExc e) {
      throw new BytecodeDbExc(e);
    }
  }

  public static <T> T wrapHashedDbExcAsBytecodeDbExc(HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new BytecodeDbExc(e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeExprNodeException(
      Hash hash, CategoryB cat, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeExprNodeExc(hash, cat, path, e);
    }
  }

  public static <T> T wrapBytecodeDbExcAsDecodeExprNodeException(
      Hash hash, CategoryB cat, String path, BytecodeDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (BytecodeDbExc e) {
      throw new DecodeExprNodeExc(hash, cat, path, e);
    }
  }

  public static <T> T wrapBytecodeDbExcAsDecodeExprNodeException(
      Hash hash, CategoryB cat, String path, int pathIndex, BytecodeDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (BytecodeDbExc e) {
      throw new DecodeExprNodeExc(hash, cat, path + "[" + pathIndex + "]", e);
    }
  }

  @FunctionalInterface
  public static interface BytecodeDbCallable<T> {
    public T call() throws BytecodeDbExc;
  }

  @FunctionalInterface
  public static interface HashedDbCallable<T> {
    public T call() throws HashedDbExc;
  }

  @FunctionalInterface
  public static interface HashedDbRunnable {
    public void run() throws HashedDbExc;
  }
}
