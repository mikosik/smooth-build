package org.smoothbuild.exec.base;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.util.concurrent.Promise;

import com.google.common.collect.ImmutableList;

public record Input(ImmutableList<ValH> vals, Hash hash) {
  public static Input fromPromises(List<Promise<ValH>> results) {
    return input(map(results, Promise::get));
  }

  public static Input input(Iterable<? extends ValH> vals) {
    return new Input(ImmutableList.copyOf(vals));
  }

  private Input(ImmutableList<ValH> vals) {
    this(vals, calculateHash(vals));
  }

  private static Hash calculateHash(Iterable<? extends ValH> vals) {
    return Hash.of(map(vals, ValH::hash));
  }
}
