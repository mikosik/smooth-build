package org.smoothbuild.db.object.obj;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.db.ObjectDbException;
import org.smoothbuild.db.object.obj.exc.DecodeObjNodeException;
import org.smoothbuild.db.object.type.base.ObjType;

public class Helpers {
  public static void wrapHashedDbExceptionAsObjectDbException(HashedDbRunnable runnable) {
    try {
      runnable.run();
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsObjectDbException(HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeObjNodeException(
      Hash hash, ObjType type, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeObjNodeException(hash, type, path, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeObjNodeException(
      Hash hash, ObjType type, String path, ObjectDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjectDbException e) {
      throw new DecodeObjNodeException(hash, type, path, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeObjNodeException(
      Hash hash, ObjType type, String path, int pathIndex, ObjectDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjectDbException e) {
      throw new DecodeObjNodeException(hash, type, path + "[" + pathIndex + "]", e);
    }
  }

  @FunctionalInterface
  public static interface ObjectDbCallable<T> {
    public T call() throws ObjectDbException;
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
