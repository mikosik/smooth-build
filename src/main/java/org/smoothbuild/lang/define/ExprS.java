package org.smoothbuild.lang.define;

import java.util.function.Function;

import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

/**
 * Expression.
 */
public sealed interface ExprS extends Nal permits MonoRefableS, InstanceS, OperatorS {
  public TypeS type();
  public ExprS mapVars(Function<VarS, TypeS> mapper);
}
