package org.smoothbuild.exec.base;

import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

public record Input(ImmutableList<Val> vals, Hash hash) {
  public static Input fromFeeders(List<Feeder<Val>> results) {
    return input(map(results, Feeder::get));
  }

  public static Input input(Iterable<? extends Val> vals) {
    return new Input(ImmutableList.copyOf(vals));
  }

  private Input(ImmutableList<Val> vals) {
    this(vals, calculateHash(vals));
  }

  private static Hash calculateHash(Iterable<? extends Val> vals) {
    return Hash.of(map(vals, Val::hash));
  }
}
