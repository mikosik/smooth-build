package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.name.Id;

public interface Item extends IdentifiableCode {
  public Maybe<Id> defaultValueId();
}
