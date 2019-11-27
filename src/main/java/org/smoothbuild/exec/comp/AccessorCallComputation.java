package org.smoothbuild.exec.comp;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.comp.ComputationHashes.accessorCallComputationHash;
import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.Container;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.type.ConcreteType;

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
