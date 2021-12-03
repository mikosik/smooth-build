package org.smoothbuild.db.object.type;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.object.db.ObjDbExc;
import org.smoothbuild.db.object.type.base.CatKindH;
import org.smoothbuild.db.object.type.exc.DecodeTypeExc;
import org.smoothbuild.db.object.type.exc.DecodeTypeNodeExc;

public class Helpers {
  public static <T> T wrapHashedDbExceptionAsDecodeTypeException(
      Hash hash, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeTypeExc(hash, e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeTypeNodeException(
      Hash hash, CatKindH kind, String path, int index, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeTypeNodeExc(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapHashedDbExceptionAsDecodeTypeNodeException(
      Hash hash, CatKindH kind, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeTypeNodeExc(hash, kind, path, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeTypeNodeException(
      CatKindH kind, Hash hash, String path, int index, ObjDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjDbExc e) {
      throw new DecodeTypeNodeExc(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapObjectDbExceptionAsDecodeTypeNodeException(
      CatKindH kind, Hash hash, String path, ObjDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjDbExc e) {
      throw new DecodeTypeNodeExc(hash, kind, path, e);
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
}
