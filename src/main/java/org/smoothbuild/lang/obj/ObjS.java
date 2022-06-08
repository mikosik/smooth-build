package org.smoothbuild.lang.obj;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.like.Obj;
import org.smoothbuild.lang.type.TypeS;

/**
 * Literal or expression in smooth language.
 */
public sealed interface ObjS extends Nal, Obj permits CnstS, ExprS {
  public TypeS type();

  @Override
  public default Optional<TypeS> typeS() {
    return Optional.of(type());
  }

  @Override
  public String name();

  @Override
  public Loc loc();
}
