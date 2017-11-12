package org.smoothbuild.task.base;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.Dag;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class Input {
  private final ImmutableList<Value> values;
  private final HashCode hash;

  public static Input fromResults(List<Dag<Task>> children) {
    return Input.fromResults(children.stream().map(Dag::elem).collect(toList()));
  }

  public static Input fromResults(Iterable<? extends Task> deps) {
    return fromValues(toResults(deps));
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

  private static ImmutableList<Value> toResults(Iterable<? extends Task> deps) {
    Builder<Value> builder = ImmutableList.builder();
    for (Task task : deps) {
      builder.add(task.output().result());
    }
    return builder.build();
  }

  private static HashCode calculateHash(Iterable<? extends Value> values) {
    Hasher hasher = Hash.newHasher();
    for (Value value : values) {
      hasher.putBytes(value.hash().asBytes());
    }
    return hasher.hash();
  }
}
