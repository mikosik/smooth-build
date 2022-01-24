package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallS(TypeS type, ExprS callable, ImmutableList<ExprS> args, Loc loc)
    implements ExprS {

  public CallS {
    checkArgument(!type.hasOpenVars());
  }

  @Override
  public String name() {
    return "()";
  }
}
