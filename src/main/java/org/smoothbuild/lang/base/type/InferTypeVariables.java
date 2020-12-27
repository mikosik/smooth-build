package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.constraint.Side.LOWER;

import java.util.List;

import org.smoothbuild.lang.base.type.constraint.VariableToBounds;

public class InferTypeVariables {
  public static VariableToBounds inferTypeVariables(
      List<Type> types, List<Type> actualTypes) {
    VariableToBounds variableToBounds = VariableToBounds.empty();
    for (int i = 0; i < types.size(); i++) {
      VariableToBounds inferred = types.get(i).inferVariableBounds(actualTypes.get(i), LOWER);
      variableToBounds = variableToBounds.mergeWith(inferred);
    }
    return variableToBounds;
  }
}
