package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.ComputationHashes.identityComputationHash;
import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

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
  public Output execute(Input input, NativeApi nativeApi) {
    return new Output(input.objects().get(0), emptyMessageArray(nativeApi));
  }
}
