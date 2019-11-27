package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.ComputationHashes.valueComputationHash;
import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.Container;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.type.ConcreteType;

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
