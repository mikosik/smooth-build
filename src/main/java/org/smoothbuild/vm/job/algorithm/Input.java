package org.smoothbuild.vm.job.algorithm;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.util.concurrent.Promise;

import com.google.common.collect.ImmutableList;

public record Input(ImmutableList<ValB> vals, Hash hash) {
  public static Input fromPromises(List<Promise<ValB>> results) {
    return input(map(results, Promise::get));
  }

  public static Input input(Iterable<? extends ValB> vals) {
    return new Input(ImmutableList.copyOf(vals));
  }

  private Input(ImmutableList<ValB> vals) {
    this(vals, calculateHash(vals));
  }

  private static Hash calculateHash(Iterable<? extends ValB> vals) {
    return Hash.of(map(vals, ValB::hash));
  }
}
