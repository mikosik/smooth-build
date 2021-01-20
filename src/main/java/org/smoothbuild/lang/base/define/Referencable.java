package org.smoothbuild.lang.base.define;

import java.util.Optional;

import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.Type;

public class Referencable extends Defined implements ReferencableLike {
  public Referencable(Type type, String name, Location location) {
    super(type, name, location);
  }

  @Override
  public Optional<Type> inferredType() {
    return Optional.of(type());
  }
}
