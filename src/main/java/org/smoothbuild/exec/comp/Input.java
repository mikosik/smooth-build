package org.smoothbuild.exec.comp;

import static com.google.common.collect.Streams.stream;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.SObject;

import com.google.common.collect.ImmutableList;

public class Input {
  private final ImmutableList<SObject> objects;
  private final Hash hash;

  public static Input input(Iterable<? extends SObject> objects) {
    return new Input(objects, calculateHash(objects));
  }

  private Input(Iterable<? extends SObject> objects, Hash hash) {
    this.objects = ImmutableList.copyOf(objects);
    this.hash = hash;
  }

  public ImmutableList<SObject> objects() {
    return objects;
  }

  public Hash hash() {
    return hash;
  }

  private static Hash calculateHash(Iterable<? extends SObject> objects) {
    return Hash.of(stream(objects).map(SObject::hash).toArray(Hash[]::new));
  }
}
