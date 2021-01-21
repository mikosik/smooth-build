package org.smoothbuild.lang.base.like;

import java.util.Optional;

import org.smoothbuild.lang.base.type.Type;

public interface ReferencableLike {
  public String name();

  public Optional<Type> inferredType();
}
