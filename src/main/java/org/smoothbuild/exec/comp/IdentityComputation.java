package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.ComputationHashes.identityComputationHash;
import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

public class IdentityComputation implements Computation {
  private final String name;
  private final ConcreteType type;

  public IdentityComputation(String name, ConcreteType type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public String name() {
    return name;
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
