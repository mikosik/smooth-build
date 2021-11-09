package org.smoothbuild.exec.base;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.util.concurrent.Promise;

import com.google.common.collect.ImmutableList;

public record Input(ImmutableList<ValueH> vals, Hash hash) {
  public static Input fromPromises(List<Promise<ValueH>> results) {
    return input(map(results, Promise::get));
  }

  public static Input input(Iterable<? extends ValueH> vals) {
    return new Input(ImmutableList.copyOf(vals));
  }

  private Input(ImmutableList<ValueH> vals) {
    this(vals, calculateHash(vals));
  }

  private static Hash calculateHash(Iterable<? extends ValueH> vals) {
    return Hash.of(map(vals, ValueH::hash));
  }
}
