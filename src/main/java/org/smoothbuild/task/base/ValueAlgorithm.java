package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.AlgorithmHashes.valueAlgorithmHash;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class ValueAlgorithm implements Algorithm {
  private final Value value;

  public ValueAlgorithm(Value value) {
    this.value = value;
  }

  public HashCode hash() {
    return valueAlgorithmHash(value);
  }

  public Type resultType() {
    return value.type();
  }

  public Output execute(Input input, ContainerImpl container) {
    return new Output(value);
  }
}
