package org.smoothbuild.exec.comp;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.Container;
import org.smoothbuild.lang.object.type.ConcreteType;

public interface Computation {
  public Hash hash();

  public ConcreteType type();

  public Output execute(Input input, Container container) throws ComputationException;
}
