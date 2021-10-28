package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;

public interface Evaluable {
  public Type evaluationType();

  public ImmutableList<Item> evaluationParameters();
}
