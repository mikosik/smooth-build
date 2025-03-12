package org.smoothbuild.compilerfrontend.lang.base;

import static org.smoothbuild.common.collect.Map.zipToMap;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeScheme;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Polymorphic referenceable.
 */
public interface PolyEvaluable extends Referenceable {
  public List<STypeVar> typeParams();

  public Evaluable evaluable();

  public default SType instantiatedType(List<SType> typeArgs) {
    var map = zipToMap(typeParams().toList(), typeArgs);
    return evaluable().type().mapTypeVars(v -> map.getOrDefault(v, v));
  }

  public STypeScheme typeScheme();
}
