package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.ComputationHashes.valueComputationHash;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

public class ValueComputation implements Computation {
  private final SObject object;

  public ValueComputation(SObject object) {
    this.object = object;
  }

  @Override
  public String name() {
    return escapedAndLimitedWithEllipsis(((SString) object).jValue(), 20);
  }

  @Override
  public Hash hash() {
    return valueComputationHash(object);
  }

  @Override
  public ConcreteType type() {
    return object.type();
  }

  @Override
  public Output execute(Input input, NativeApi nativeApi) {
    return new Output(object, nativeApi.messages());
  }
}
