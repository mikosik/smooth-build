package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallS(TypeS type, MonoObjS callee, ImmutableList<MonoObjS> args, Loc loc) implements
    MonoExprS {
  @Override
  public String name() {
    return "()";
  }
}
