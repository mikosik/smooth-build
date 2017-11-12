package org.smoothbuild.task.base;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.Dag;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class Input {
  private final ImmutableList<Value> values;
  private final HashCode hash;

  public static Input fromResults(List<Dag<Task>> children) {
    return fromValues(toResults(children));
  }

  public static Input fromValues(Iterable<? extends Value> values) {
    return new Input(values, calculateHash(values));
  }

  private Input(Iterable<? extends Value> values, HashCode hash) {
    this.values = ImmutableList.copyOf(values);
    this.hash = hash;
  }

  public ImmutableList<Value> values() {
    return values;
  }

  public HashCode hash() {
    return hash;
  }

  private static ImmutableList<Value> toResults(List<Dag<Task>> deps) {
    return deps.stream()
        .map(t -> t.elem().output().result())
        .collect(toImmutableList());
  }

  private static HashCode calculateHash(Iterable<? extends Value> values) {
    Hasher hasher = Hash.newHasher();
    for (Value value : values) {
      hasher.putBytes(value.hash().asBytes());
    }
    return hasher.hash();
  }
}
