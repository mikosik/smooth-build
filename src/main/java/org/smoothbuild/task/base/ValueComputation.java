package org.smoothbuild.task.base;

import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;
import static org.smoothbuild.task.base.ComputationHashes.valueComputationHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.task.exec.Container;

public class ValueComputation implements Computation {
  private final SObject object;

  public ValueComputation(SObject object) {
    this.object = object;
  }

  @Override
  public Hash hash() {
    return valueComputationHash(object);
  }

  @Override
  public ConcreteType type() {
    return object.type();
  }

  @Override
  public Output execute(Input input, Container container) {
    return new Output(object, emptyMessageArray(container));
  }
}
