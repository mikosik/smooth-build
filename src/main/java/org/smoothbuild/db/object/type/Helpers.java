package org.smoothbuild.db.object.type;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.db.ObjectDbException;
import org.smoothbuild.db.object.type.base.ObjKind;
import org.smoothbuild.db.object.type.exc.DecodeTypeException;
import org.smoothbuild.db.object.type.exc.DecodeTypeNodeException;

public class Helpers {
  public static <T> T wrapHashedDbExceptionAsDecodeTypeException(
      Hash hash, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeTypeException(hash, e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeTypeNodeException(
      Hash hash, ObjKind objKind, String path, int index, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeTypeNodeException(hash, objKind, path, index, e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeTypeNodeException(
      Hash hash, ObjKind objKind, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeTypeNodeException(hash, objKind, path, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeTypeNodeException(
      ObjKind objKind, Hash hash, String path, int index, ObjectDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjectDbException e) {
      throw new DecodeTypeNodeException(hash, objKind, path, index, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeTypeNodeException(
      ObjKind objKind, Hash hash, String path, ObjectDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjectDbException e) {
      throw new DecodeTypeNodeException(hash, objKind, path, e);
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
