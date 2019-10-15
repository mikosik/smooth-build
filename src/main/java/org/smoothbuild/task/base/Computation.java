package org.smoothbuild.task.base;

import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.exec.Container;

import com.google.common.hash.HashCode;

public interface Computation {
  public HashCode hash();

  public ConcreteType type();

  public Output execute(Input input, Container container) throws ComputationException;
}
