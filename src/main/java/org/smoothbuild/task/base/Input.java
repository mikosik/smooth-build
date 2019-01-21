package org.smoothbuild.task.base;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class Input {
  private final ImmutableList<Value> values;
  private final HashCode hash;

  public static Input fromResults(List<Task> children) {
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

  private static ImmutableList<Value> toResults(List<Task> deps) {
    return deps.stream()
        .map(t -> t.output().result())
        .collect(toImmutableList());
  }

  private static HashCode calculateHash(Iterable<? extends Value> values) {
    return Hash.hashes(stream(values).map(Value::hash).toArray(HashCode[]::new));
  }
}
