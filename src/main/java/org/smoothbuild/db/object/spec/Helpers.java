package org.smoothbuild.db.object.spec;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.db.ObjectDbException;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.db.object.spec.exc.DecodeSpecException;
import org.smoothbuild.db.object.spec.exc.DecodeSpecNodeException;

public class Helpers {
  public static <T> T wrapHashedDbExceptionAsDecodeSpecException(
      Hash hash, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeSpecException(hash, e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeSpecNodeException(
      Hash hash, SpecKind specKind, String path, int index, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeSpecNodeException(hash, specKind, path, index, e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeSpecNodeException(
      Hash hash, SpecKind specKind, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeSpecNodeException(hash, specKind, path, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeSpecNodeException(
      SpecKind specKind, Hash hash, String path, int index, ObjectDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjectDbException e) {
      throw new DecodeSpecNodeException(hash, specKind, path, index, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeSpecNodeException(
      SpecKind specKind, Hash hash, String path, ObjectDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjectDbException e) {
      throw new DecodeSpecNodeException(hash, specKind, path, e);
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
}
