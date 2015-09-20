package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.AlgorithmHashes.identityAlgorithmHash;

import org.smoothbuild.lang.type.Type;
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
  public Output execute(Input input, ContainerImpl container) {
    return new Output(input.values().get(0));
  }
}
