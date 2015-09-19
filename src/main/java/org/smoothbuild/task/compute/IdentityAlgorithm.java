package org.smoothbuild.task.compute;

import static org.smoothbuild.task.compute.AlgorithmHashes.identityAlgorithmHash;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class IdentityAlgorithm implements Algorithm {
  private final Type type;

  public IdentityAlgorithm(Type type) {
    this.type = type;
  }

  @Override
  public HashCode hash() {
    return identityAlgorithmHash();
  }

  @Override
  public Type resultType() {
    return type;
  }

  @Override
  public TaskOutput execute(Input input, ContainerImpl container) {
    return new TaskOutput(input.values().get(0));
  }
}
