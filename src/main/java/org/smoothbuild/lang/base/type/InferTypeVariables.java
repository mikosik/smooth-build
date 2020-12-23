package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.constraint.Side.LOWER;

import java.util.List;

import org.smoothbuild.lang.base.type.constraint.Constraints;

public class InferTypeVariables {
  public static Constraints inferTypeVariables(
      List<Type> types, List<Type> actualTypes) {
    Constraints constraints = Constraints.empty();
    for (int i = 0; i < types.size(); i++) {
      Constraints inferred = types.get(i).inferConstraints(actualTypes.get(i), LOWER);
      constraints = constraints.mergeWith(inferred);
    }
    return constraints;
  }
}
