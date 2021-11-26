package org.smoothbuild.lang.base.define;


import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

public sealed interface EvaluableS extends Nal
    permits DefinedEvaluableS, NativeEvaluableS, TopEvaluableS {
  public TypeS evaluationType();

  public NList<Item> evaluationParameters();

  public String extendedName();
}
