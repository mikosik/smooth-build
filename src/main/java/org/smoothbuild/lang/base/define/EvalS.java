package org.smoothbuild.lang.base.define;

import java.util.Optional;

import org.smoothbuild.lang.base.like.EvalLike;
import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * Evaluable.
 */
public class EvalS extends Defined implements EvalLike {
  public EvalS(TypeS type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }

  @Override
  public Optional<TypeS> inferredType() {
    return Optional.of(type());
  }
}
