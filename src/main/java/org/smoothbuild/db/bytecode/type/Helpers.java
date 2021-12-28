package org.smoothbuild.db.bytecode.type;

import org.smoothbuild.db.bytecode.db.ByteDbExc;
import org.smoothbuild.db.bytecode.type.base.CatKindB;
import org.smoothbuild.db.bytecode.type.exc.DecodeCatExc;
import org.smoothbuild.db.bytecode.type.exc.DecodeCatNodeExc;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.exc.HashedDbExc;

public class Helpers {
  public static <T> T wrapHashedDbExcAsDecodeCatExc(Hash hash, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeCatExc(hash, e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeCatNodeExc(
      Hash hash, CatKindB kind, String path, int index, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeCatNodeExc(
      Hash hash, CatKindB kind, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, e);
    }
  }

  public static <T> T wrapObjectDbExcAsDecodeCatNodeExc(
      CatKindB kind, Hash hash, String path, int index, ByteDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ByteDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapObjectDbExcAsDecodeCatNodeExc(
      CatKindB kind, Hash hash, String path, ByteDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (ByteDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, e);
    }
  }

  @FunctionalInterface
  public static interface ByteDbCallable<T> {
    public T call() throws ByteDbExc;
  }

  @FunctionalInterface
  public static interface HashedDbCallable<T> {
    public T call() throws HashedDbExc;
  }
}
