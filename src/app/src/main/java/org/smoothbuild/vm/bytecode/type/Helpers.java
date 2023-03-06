package org.smoothbuild.vm.bytecode.type;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.vm.bytecode.type.exc.CategoryDbExc;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatExc;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatNodeExc;

public class Helpers {
  public static <T> T wrapHashedDbExcAsDecodeCatExc(Hash hash, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeCatExc(hash, e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeCatNodeExc(
      Hash hash, CategoryKindB kind, String path, int index, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeCatNodeExc(
      Hash hash, CategoryKindB kind, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, e);
    }
  }

  public static <T> T wrapCatDbExcAsDecodeCatNodeExc(
      CategoryKindB kind, Hash hash, String path, int index, CategoryDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (CategoryDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapCatDbExcAsDecodeCatNodeExc(
      CategoryKindB kind, Hash hash, String path, CategoryDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (CategoryDbExc e) {
      throw new DecodeCatNodeExc(hash, kind, path, e);
    }
  }

  @FunctionalInterface
  public static interface CategoryDbCallable<T> {
    public T call() throws CategoryDbExc;
  }

  @FunctionalInterface
  public static interface HashedDbCallable<T> {
    public T call() throws HashedDbExc;
  }
}
