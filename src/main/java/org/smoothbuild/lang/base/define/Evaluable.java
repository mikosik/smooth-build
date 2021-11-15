package org.smoothbuild.lang.base.define;


import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

public interface Evaluable {
  public TypeS evaluationType();

  public NList<Item> evaluationParameters();
}
