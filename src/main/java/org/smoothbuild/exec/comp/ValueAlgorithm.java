package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.valueAlgorithmHash;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

public class ValueAlgorithm implements Algorithm {
  private final SObject object;

  public ValueAlgorithm(SObject object) {
    this.object = object;
  }

  @Override
  public String name() {
    return escapedAndLimitedWithEllipsis(((SString) object).jValue(), 20);
  }

  @Override
  public Hash hash() {
    return valueAlgorithmHash(object);
  }

  @Override
  public ConcreteType type() {
    return object.type();
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    return new Output(object, nativeApi.messages());
  }
}
