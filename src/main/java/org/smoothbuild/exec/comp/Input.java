package org.smoothbuild.exec.comp;

import static com.google.common.collect.Streams.stream;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.base.SObject;

import com.google.common.collect.ImmutableList;

public record Input(ImmutableList<SObject>objects, Hash hash) {

  public static Input input(Iterable<? extends SObject> objects) {
    return new Input(ImmutableList.copyOf(objects));
  }

  private Input(ImmutableList<SObject> objects) {
    this(objects, calculateHash(objects));
  }

  private static Hash calculateHash(Iterable<? extends SObject> objects) {
    return Hash.of(stream(objects).map(SObject::hash).toArray(Hash[]::new));
  }
}
