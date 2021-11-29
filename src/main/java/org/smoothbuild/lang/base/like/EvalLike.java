package org.smoothbuild.lang.base.like;

import java.util.Optional;

import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.Named;

public interface EvalLike extends Named {
  public Optional<TypeS> inferredType();
}
