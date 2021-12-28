package org.smoothbuild.db.bytecode.obj;

import org.smoothbuild.db.bytecode.db.ByteDbExc;
import org.smoothbuild.db.bytecode.obj.exc.DecodeObjNodeExc;
import org.smoothbuild.db.bytecode.type.base.CatB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbExc;

public class Helpers {
  public static void wrapHashedDbExceptionAsObjectDbException(HashedDbRunnable runnable) {
    try {
      runnable.run();
    } catch (HashedDbExc e) {
      throw new ByteDbExc(e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsObjectDbException(HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new ByteDbExc(e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeObjNodeException(
      Hash hash, CatB cat, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeObjNodeExc(hash, cat, path, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeObjNodeException(
      Hash hash, CatB cat, String path, ByteDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ByteDbExc e) {
      throw new DecodeObjNodeExc(hash, cat, path, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeObjNodeException(
      Hash hash, CatB cat, String path, int pathIndex, ByteDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ByteDbExc e) {
      throw new DecodeObjNodeExc(hash, cat, path + "[" + pathIndex + "]", e);
    }
  }

  @FunctionalInterface
  public static interface ByteDbCallable<T> {
    public T call() throws ByteDbExc;
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
