package org.smoothbuild.lang.base.define;


import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

public interface EvaluableS extends Nal {
  public TypeS evaluationType();

  public NList<Item> evaluationParameters();
}
