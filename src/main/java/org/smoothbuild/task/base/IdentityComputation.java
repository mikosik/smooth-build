package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.ComputationHashes.identityComputationHash;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.exec.Container;

import com.google.common.hash.HashCode;

public class IdentityComputation implements Computation {
  private final Type type;

  public IdentityComputation(Type type) {
    this.type = type;
  }

  @Override
  public HashCode hash() {
    return identityComputationHash();
  }

  @Override
  public Type resultType() {
    return type;
  }

  @Override
  public Output execute(Input input, Container container) {
    return new Output(input.values().get(0));
  }
}
