package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.ComputationHashes.identityComputationHash;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class IdentityComputation implements Computation {
  private final Type type;

  public IdentityComputation(Type type) {
    this.type = type;
  }

  public HashCode hash() {
    return identityComputationHash();
  }

  public Type resultType() {
    return type;
  }

  public Output execute(Input input, ContainerImpl container) {
    return new Output(input.values().get(0));
  }
}
