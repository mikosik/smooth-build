package org.smoothbuild.lang.base.define;


import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NamedList;

public interface Evaluable {
  public TypeS evaluationType();

  public NamedList<Item> evaluationParameters();
}
