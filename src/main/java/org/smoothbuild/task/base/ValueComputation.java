package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.ComputationHashes.valueComputationHash;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class ValueComputation implements Computation {
  private final Value value;

  public ValueComputation(Value value) {
    this.value = value;
  }

  public HashCode hash() {
    return valueComputationHash(value);
  }

  public Type resultType() {
    return value.type();
  }

  public Output execute(Input input, ContainerImpl container) {
    return new Output(value);
  }
}
