package org.smoothbuild.task.base;

import static org.smoothbuild.lang.message.Messages.emptyMessageArray;
import static org.smoothbuild.task.base.ComputationHashes.identityComputationHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.exec.Container;

public class IdentityComputation implements Computation {
  private final ConcreteType type;

  public IdentityComputation(ConcreteType type) {
    this.type = type;
  }

  @Override
  public Hash hash() {
    return identityComputationHash();
  }

  @Override
  public ConcreteType type() {
    return type;
  }

  @Override
  public Output execute(Input input, Container container) {
    return new Output(input.values().get(0), emptyMessageArray(container));
  }
}
