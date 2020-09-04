package org.smoothbuild.exec.base;

import static com.google.common.collect.Streams.stream;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Obj;

import com.google.common.collect.ImmutableList;

public record Input(ImmutableList<Obj> objects, Hash hash) {

  public static Input input(Iterable<? extends Obj> objects) {
    return new Input(ImmutableList.copyOf(objects));
  }

  private Input(ImmutableList<Obj> objects) {
    this(objects, calculateHash(objects));
  }

  private static Hash calculateHash(Iterable<? extends Obj> objects) {
    return Hash.of(stream(objects).map(Obj::hash).toArray(Hash[]::new));
  }
}
