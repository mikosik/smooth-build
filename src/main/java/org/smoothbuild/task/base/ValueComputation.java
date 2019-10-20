package org.smoothbuild.task.base;

import static org.smoothbuild.lang.message.Messages.emptyMessageArray;
import static org.smoothbuild.task.base.ComputationHashes.valueComputationHash;

import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.Container;

import com.google.common.hash.HashCode;

public class ValueComputation implements Computation {
  private final Value value;

  public ValueComputation(Value value) {
    this.value = value;
  }

  @Override
  public HashCode hash() {
    return valueComputationHash(value);
  }

  @Override
  public ConcreteType type() {
    return value.type();
  }

  @Override
  public Output execute(Input input, Container container) {
    return new Output(value, emptyMessageArray(container));
  }
}
