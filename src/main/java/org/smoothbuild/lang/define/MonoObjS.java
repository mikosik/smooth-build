package org.smoothbuild.lang.define;

import java.util.Optional;

import org.smoothbuild.lang.like.MonoObj;
import org.smoothbuild.lang.type.TypeS;

/**
 * Monomorphic object.
 */
public sealed interface MonoObjS extends ObjS, MonoObj permits MonoRefableObjS, CnstS, MonoExprS {
  @Override
  public TypeS type();

  @Override
  public default Optional<TypeS> typeO() {
    return Optional.of(type());
  }
}
