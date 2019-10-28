package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;
import static org.smoothbuild.task.base.ComputationHashes.accessorCallComputationHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.task.exec.Container;

import com.google.common.collect.ImmutableList;

public class AccessorCallComputation implements Computation {
  private final Accessor accessor;

  public AccessorCallComputation(Accessor accessor) {
    this.accessor = accessor;
  }

  @Override
  public Hash hash() {
    return accessorCallComputationHash(accessor);
  }

  @Override
  public ConcreteType type() {
    return accessor.type();
  }

  @Override
  public Output execute(Input input, Container container) {
    ImmutableList<SObject> objects = input.objects();
    checkArgument(objects.size() == 1);
    Struct struct = (Struct) objects.get(0);
    return new Output(struct.get(accessor.fieldName()), emptyMessageArray(container));
  }
}
