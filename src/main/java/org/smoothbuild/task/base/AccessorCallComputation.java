package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.task.base.ComputationHashes.accessorCallComputationHash;

import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.Container;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class AccessorCallComputation implements Computation {
  private final Accessor accessor;

  public AccessorCallComputation(Accessor accessor) {
    this.accessor = accessor;
  }

  @Override
  public HashCode hash() {
    return accessorCallComputationHash(accessor);
  }

  @Override
  public ConcreteType type() {
    return accessor.type();
  }

  @Override
  public Output execute(Input input, Container container) {
    ImmutableList<Value> values = input.values();
    checkArgument(values.size() == 1);
    Struct struct = (Struct) values.get(0);
    return new Output(struct.get(accessor.fieldName()));
  }
}
