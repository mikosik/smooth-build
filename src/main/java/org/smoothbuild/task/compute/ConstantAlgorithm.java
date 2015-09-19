package org.smoothbuild.task.compute;

import static org.smoothbuild.task.compute.AlgorithmHashes.constantAlgorithmHash;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class ConstantAlgorithm implements Algorithm {
  private final Value value;

  public ConstantAlgorithm(Value value) {
    this.value = value;
  }

  @Override
  public HashCode hash() {
    return constantAlgorithmHash(value);
  }

  @Override
  public Type resultType() {
    return value.type();
  }

  @Override
  public TaskOutput execute(Input input, ContainerImpl container) {
    return new TaskOutput(value);
  }
}
