package org.smoothbuild.vm.bytecode.expr;

import org.smoothbuild.vm.bytecode.expr.exc.BytecodeDbException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.vm.bytecode.type.CategoryB;

public class Helpers {
  public static void wrapHashedDbExcAsBytecodeDbExc(HashedDbRunnable runnable) {
    try {
      runnable.run();
    } catch (HashedDbException e) {
      throw new BytecodeDbException(e);
    }
  }

  public static <T> T wrapHashedDbExcAsBytecodeDbExc(HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new BytecodeDbException(e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeExprNodeException(
      Hash hash, CategoryB cat, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeExprNodeException(hash, cat, path, e);
    }
  }

  public static <T> T wrapBytecodeDbExcAsDecodeExprNodeException(
      Hash hash, CategoryB cat, String path, BytecodeDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (BytecodeDbException e) {
      throw new DecodeExprNodeException(hash, cat, path, e);
    }
  }

  public static <T> T wrapBytecodeDbExcAsDecodeExprNodeException(
      Hash hash, CategoryB cat, String path, int pathIndex, BytecodeDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (BytecodeDbException e) {
      throw new DecodeExprNodeException(hash, cat, path + "[" + pathIndex + "]", e);
    }
  }

  @FunctionalInterface
  public static interface BytecodeDbCallable<T> {
    public T call() throws BytecodeDbException;
  }

  @FunctionalInterface
  public static interface HashedDbCallable<T> {
    public T call() throws HashedDbException;
  }

  @FunctionalInterface
  public static interface HashedDbRunnable {
    public void run() throws HashedDbException;
  }
}
