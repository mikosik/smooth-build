package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.exc.DecodeObjNodeException;
import org.smoothbuild.db.object.exc.DecodeSpecException;
import org.smoothbuild.db.object.exc.ObjectDbException;
import org.smoothbuild.db.object.spec.base.Spec;

public class Helpers {
  public static <T> T wrapObjectDbExceptionAsDecodeObjNodeException(
      Hash hash, Spec spec, String memberPath, ObjectDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjectDbException e) {
      throw new DecodeObjNodeException(hash, spec, memberPath, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeObjNodeException(
      Hash hash, Spec spec, String memberPath, int pathIndex, ObjectDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjectDbException e) {
      throw new DecodeObjNodeException(hash, spec, memberPath + "[" + pathIndex + "]", e);
    }
  }

  @FunctionalInterface
  public static interface ObjectDbCallable<T> {
    public T call() throws ObjectDbException;
  }

  public static <T> T wrapHashedDbExceptionAsObjectDbException(HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeObjNodeException(
      Hash hash, Spec spec, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeObjNodeException(hash, spec, path, e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeSpecException(
      Hash hash, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeSpecException(hash, e);
    }
  }

  @FunctionalInterface
  public static interface HashedDbCallable<T> {
    public T call() throws HashedDbException;
  }

  public static void wrapHashedDbExceptionAsObjectDbException(HashedDbRunnable runnable) {
    try {
      runnable.run();
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  @FunctionalInterface
  public static interface HashedDbRunnable {
    public void run() throws HashedDbException;
  }
}
