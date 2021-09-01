package org.smoothbuild.exec.base;

import static org.smoothbuild.util.Lists.map;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Obj;

import com.google.common.collect.ImmutableList;

public record Input(ImmutableList<Obj> objects, Hash hash) {

  public static Input input(Iterable<? extends Obj> objects) {
    return new Input(ImmutableList.copyOf(objects));
  }

  private Input(ImmutableList<Obj> objects) {
    this(objects, calculateHash(objects));
  }

  private static Hash calculateHash(Iterable<? extends Obj> objects) {
    return Hash.of(map(objects, Obj::hash));
  }
}
