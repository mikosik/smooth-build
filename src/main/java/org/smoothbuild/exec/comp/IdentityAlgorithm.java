package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.identityAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

public class IdentityAlgorithm implements Algorithm {
  private final String name;
  private final ConcreteType type;

  public IdentityAlgorithm(String name, ConcreteType type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Hash hash() {
    return identityAlgorithmHash();
  }

  @Override
  public ConcreteType type() {
    return type;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    return new Output(input.objects().get(0), nativeApi.messages());
  }
}
