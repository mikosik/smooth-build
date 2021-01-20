package org.smoothbuild.lang.base.like;

import java.util.Optional;

import org.smoothbuild.lang.base.type.Type;

public interface ReferencableLike {
  public Optional<Type> inferredType();
}
