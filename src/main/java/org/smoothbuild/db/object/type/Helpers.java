package org.smoothbuild.db.object.type;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.object.db.ObjDbExc;
import org.smoothbuild.db.object.type.base.CatKindH;
import org.smoothbuild.db.object.type.exc.DecodeCatExc;
import org.smoothbuild.db.object.type.exc.DecodeCatNodeExc;

public class Helpers {
  public static <T> T wrapHashedDbExcAsDecodeCatExc(Hash hash, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeCatExc(hash, e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeCatNodeExc(
      Hash hash, CatKindH kind, String path, int index, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeCatNodeExc(
      Hash hash, CatKindH kind, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, e);
    }
  }

  public static <T> T wrapObjectDbExcAsDecodeCatNodeExc(
      CatKindH kind, Hash hash, String path, int index, ObjDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapObjectDbExcAsDecodeCatNodeExc(
      CatKindH kind, Hash hash, String path, ObjDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ObjDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, e);
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
