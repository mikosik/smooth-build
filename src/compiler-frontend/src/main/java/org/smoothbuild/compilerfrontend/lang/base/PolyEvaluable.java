package org.smoothbuild.compilerfrontend.lang.base;

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

  public SType instantiatedType(List<SType> typeArgs);

  public STypeScheme typeScheme();
}
