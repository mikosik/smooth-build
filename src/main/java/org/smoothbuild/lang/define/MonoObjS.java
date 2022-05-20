package org.smoothbuild.lang.define;

import java.util.Optional;

import org.smoothbuild.lang.like.MonoObj;
import org.smoothbuild.lang.type.MonoTS;

/**
 * Monomorphic object.
 */
public sealed interface MonoObjS extends ObjS, MonoObj permits MonoTopRefableS, CnstS, MonoExprS {
  @Override
  public MonoTS type();

  @Override
  public default Optional<? extends MonoTS> typeO() {
    return Optional.of(type());
  }
}
