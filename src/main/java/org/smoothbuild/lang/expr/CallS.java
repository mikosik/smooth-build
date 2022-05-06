package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallS(TypeS type, ExprS callable, ImmutableList<ExprS> args, Loc loc)
    implements ExprS {
  @Override
  public String name() {
    return "()";
  }
}
