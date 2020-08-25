package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.type.Type;

public interface RefTarget {
  public Optional<Type> inferredType();
}
