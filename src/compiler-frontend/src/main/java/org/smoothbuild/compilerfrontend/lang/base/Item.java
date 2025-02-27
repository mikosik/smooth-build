package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.collect.Maybe;

public interface Item extends IdentifiableCode {
  public Maybe<? extends DefaultValue> defaultValue();
}
