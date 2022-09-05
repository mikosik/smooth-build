package org.smoothbuild.bytecode.type;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.bytecode.type.exc.CatDbExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatNodeExc;

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

  public static <T> T wrapCatDbExcAsDecodeCatNodeExc(
      CatKindB kind, Hash hash, String path, int index, CatDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (CatDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapCatDbExcAsDecodeCatNodeExc(
      CatKindB kind, Hash hash, String path, CatDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (CatDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, e);
    }
  }

  @FunctionalInterface
  public static interface CatDbCallable<T> {
    public T call() throws CatDbExc;
  }

  @FunctionalInterface
  public static interface HashedDbCallable<T> {
    public T call() throws HashedDbExc;
  }
}
