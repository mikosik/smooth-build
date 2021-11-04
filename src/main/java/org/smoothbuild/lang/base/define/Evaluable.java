package org.smoothbuild.lang.base.define;


import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;

public interface Evaluable {
  public TypeS evaluationType();

  public ImmutableList<Item> evaluationParameters();
}
