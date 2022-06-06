package org.smoothbuild.lang.obj;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallS(TypeS type, ObjS callee, ImmutableList<ObjS> args, Loc loc) implements ExprS {
  @Override
  public String name() {
    return "()";
  }
}
