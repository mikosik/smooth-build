package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.ComputationHashes.valueComputationHash;
import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;
import static org.smoothbuild.util.Strings.escaped;

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
    String escaped = escaped(((SString) object).jValue());
    int limit = 20;
    if (limit < (escaped.length() + 2)) {
      return "\"" + escaped.substring(0, limit - 5) + "\"...";
    } else {
      return "\"" + escaped + "\"";
    }
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
    return new Output(object, emptyMessageArray(nativeApi));
  }
}
