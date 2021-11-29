package org.smoothbuild.lang.base.define;

import java.util.Optional;

import org.smoothbuild.lang.base.like.EvalLike;
import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * Evaluable.
 */
public class EvalS extends Defined implements EvalLike {
  public EvalS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }

  @Override
  public Optional<TypeS> inferredType() {
    return Optional.of(type());
  }
}
