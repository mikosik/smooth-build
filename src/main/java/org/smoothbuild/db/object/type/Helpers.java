package org.smoothbuild.db.object.type;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.db.ObjDbException;
import org.smoothbuild.db.object.type.base.SpecKindH;
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
      Hash hash, SpecKindH kind, String path, int index, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeTypeNodeException(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeTypeNodeException(
      Hash hash, SpecKindH kind, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeTypeNodeException(hash, kind, path, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeTypeNodeException(
      SpecKindH kind, Hash hash, String path, int index, ObjDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjDbException e) {
      throw new DecodeTypeNodeException(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeTypeNodeException(
      SpecKindH kind, Hash hash, String path, ObjDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjDbException e) {
      throw new DecodeTypeNodeException(hash, kind, path, e);
    }
  }
  @FunctionalInterface
  public static interface ObjDbCallable<T> {
    public T call() throws ObjDbException;
  }

  @FunctionalInterface
  public static interface HashedDbCallable<T> {
    public T call() throws HashedDbException;
  }
}
