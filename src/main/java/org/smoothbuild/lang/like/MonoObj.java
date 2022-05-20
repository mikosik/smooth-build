package org.smoothbuild.lang.like;

import java.util.Optional;

import org.smoothbuild.lang.type.MonoTS;

/**
 * Monomorphic object.
 */
public interface MonoObj extends Obj {
  @Override
  public Optional<? extends MonoTS> typeO();
}
