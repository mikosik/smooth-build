package org.smoothbuild.vm.bytecode.type;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.vm.bytecode.type.exc.CategoryDbException;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatException;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatNodeException;

public class Helpers {
  public static <T> T wrapHashedDbExcAsDecodeCatExc(Hash hash, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeCatException(hash, e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeCatNodeExc(
      Hash hash, CategoryKindB kind, String path, int index, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeCatNodeException(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapHashedDbExcAsDecodeCatNodeExc(
      Hash hash, CategoryKindB kind, String path, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new DecodeCatNodeException(hash, kind, path, e);
    }
  }

  public static <T> T wrapCatDbExcAsDecodeCatNodeExc(
      CategoryKindB kind, Hash hash, String path, int index, CategoryDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (CategoryDbException e) {
      throw new DecodeCatNodeException(hash, kind, path, index, e);
    }
  }

  public static <T> T wrapCatDbExcAsDecodeCatNodeExc(
      CategoryKindB kind, Hash hash, String path, CategoryDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (CategoryDbException e) {
      throw new DecodeCatNodeException(hash, kind, path, e);
    }
  }

  @FunctionalInterface
  public static interface CategoryDbCallable<T> {
    public T call() throws CategoryDbException;
  }

  @FunctionalInterface
  public static interface HashedDbCallable<T> {
    public T call() throws HashedDbException;
  }
}
