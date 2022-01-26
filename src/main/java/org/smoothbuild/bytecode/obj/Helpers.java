package org.smoothbuild.bytecode.obj;

import org.smoothbuild.bytecode.obj.exc.DecodeObjNodeExc;
import org.smoothbuild.bytecode.obj.exc.ObjDbExc;
import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.db.exc.HashedDbExc;

public class Helpers {
  public static void wrapHashedDbExcAsObjDbExc(HashedDbRunnable runnable) {
    try {
      runnable.run();
    } catch (HashedDbExc e) {
      throw new ObjDbExc(e);
    }
  }

  public static <T> T wrapHashedDbExcAsObjDbExc(HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new ObjDbExc(e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeObjNodeException(
      Hash hash, CatB cat, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeObjNodeExc(hash, cat, path, e);
    }
  }

  public static <T> T wrapObjDbExcAsDecodeObjNodeException(
      Hash hash, CatB cat, String path, objDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjDbExc e) {
      throw new DecodeObjNodeExc(hash, cat, path, e);
    }
  }

  public static <T> T wrapObjDbExcAsDecodeObjNodeException(
      Hash hash, CatB cat, String path, int pathIndex, objDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjDbExc e) {
      throw new DecodeObjNodeExc(hash, cat, path + "[" + pathIndex + "]", e);
    }
  }

  @FunctionalInterface
  public static interface objDbCallable<T> {
    public T call() throws ObjDbExc;
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
