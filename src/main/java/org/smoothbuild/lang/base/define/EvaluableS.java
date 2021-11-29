package org.smoothbuild.lang.base.define;


import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

public sealed interface EvaluableS extends Nal permits DefinedEvaluableS, TopEvaluableS {
  public TypeS evaluationType();

  public NList<Item> evaluationParams();

  public String extendedName();
}
