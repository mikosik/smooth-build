package org.smoothbuild.lang.like;

import java.util.Optional;

import org.smoothbuild.lang.type.TypeS;

public interface Expr {
  public Optional<TypeS> typeO();
}
