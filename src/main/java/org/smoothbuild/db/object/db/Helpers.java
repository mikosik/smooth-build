package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.exc.DecodeObjNodeException;
import org.smoothbuild.db.object.exc.DecodeSpecException;
import org.smoothbuild.db.object.exc.DecodeSpecNodeException;
import org.smoothbuild.db.object.exc.ObjectDbException;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;

public class Helpers {
  // wrapping Callables

  public static <T> T wrapObjectDbExceptionAsDecodeObjNodeException(
      Hash hash, Spec spec, String nodePath, ObjectDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjectDbException e) {
      throw new DecodeObjNodeException(hash, spec, nodePath, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeObjNodeException(
      Hash hash, Spec spec, String nodePath, int pathIndex, ObjectDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjectDbException e) {
      throw new DecodeObjNodeException(hash, spec, nodePath + "[" + pathIndex + "]", e);
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
      Hash hash, Spec spec, String nodePath, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeObjNodeException(hash, spec, nodePath, e);
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

  public static <T> T wrapHashedDbExceptionAsDecodeSpecNodeException(
      Hash hash, SpecKind specKind, String nodePath, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeSpecNodeException(hash, specKind, nodePath, e);
    }
  }

  @FunctionalInterface
  public static interface HashedDbCallable<T> {
    public T call() throws HashedDbException;
  }

  // wrapping Runnable

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
