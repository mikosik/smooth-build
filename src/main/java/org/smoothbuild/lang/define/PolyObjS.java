package org.smoothbuild.lang.define;

import java.util.Optional;

import org.smoothbuild.lang.like.PolyObj;
import org.smoothbuild.lang.type.PolyTS;

/**
 * Polymorphic object.
 */
public sealed interface PolyObjS extends ObjS, PolyObj
  permits PolyTopRefableS {

  @Override
  public PolyTS type();

  @Override
  public default Optional<PolyTS> typeO() {
    return Optional.of(type());
  }
}
