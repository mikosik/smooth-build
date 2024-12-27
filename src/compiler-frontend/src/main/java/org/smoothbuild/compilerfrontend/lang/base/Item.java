package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public interface Item extends HasName {
  @Override
  public Name name();

  public Maybe<Id> defaultValueId();
}
