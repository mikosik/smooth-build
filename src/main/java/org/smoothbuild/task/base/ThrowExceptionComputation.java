package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.ComputationHashes.throwExceptionComputationHash;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.exec.Container;

import com.google.common.hash.HashCode;

public class ThrowExceptionComputation implements Computation {
  private final Type type;

  public ThrowExceptionComputation(Type type) {
    this.type = type;
  }

  @Override
  public HashCode hash() {
    return throwExceptionComputationHash();
  }

  @Override
  public Type resultType() {
    return type;
  }

  @Override
  public Output execute(Input input, Container container) {
    throw new RuntimeException("This should not happen. It means smooth build release is broken.");
  }
}
