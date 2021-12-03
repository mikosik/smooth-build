package org.smoothbuild.db.object.obj;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.object.db.ObjDbExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjNodeExc;
import org.smoothbuild.db.object.type.base.CatH;

public class Helpers {
  public static void wrapHashedDbExceptionAsObjectDbException(HashedDbRunnable runnable) {
    try {
      runnable.run();
    } catch (HashedDbExc e) {
      throw new ObjDbExc(e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsObjectDbException(HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new ObjDbExc(e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeObjNodeException(
      Hash hash, CatH cat, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeObjNodeExc(hash, cat, path, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeObjNodeException(
      Hash hash, CatH cat, String path, ObjDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjDbExc e) {
      throw new DecodeObjNodeExc(hash, cat, path, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeObjNodeException(
      Hash hash, CatH cat, String path, int pathIndex, ObjDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjDbExc e) {
      throw new DecodeObjNodeExc(hash, cat, path + "[" + pathIndex + "]", e);
    }
  }

  @FunctionalInterface
  public static interface ObjDbCallable<T> {
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
