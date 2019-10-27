package org.smoothbuild.task.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.exec.Container;

public interface Computation {
  public Hash hash();

  public ConcreteType type();

  public Output execute(Input input, Container container) throws ComputationException;
}
