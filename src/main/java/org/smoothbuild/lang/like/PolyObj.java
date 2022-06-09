package org.smoothbuild.lang.like;

import java.util.Optional;

import org.smoothbuild.lang.type.PolyTS;

/**
 * Polymorphic object.
 */
public interface PolyObj extends Obj {
  @Override
  public Optional<PolyTS> typeO();
}
