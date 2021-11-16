package org.smoothbuild.lang.base.define;

import java.util.Optional;

import org.smoothbuild.lang.base.like.EvaluableLike;
import org.smoothbuild.lang.base.type.impl.TypeS;

public class EvaluableImplS extends Defined implements EvaluableLike {
  public EvaluableImplS(TypeS type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }

  @Override
  public Optional<TypeS> inferredType() {
    return Optional.of(type());
  }
}
