package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NamedList;

/**
 * This class is immutable.
 */
public abstract class ValueS extends GlobalReferencable {
  public ValueS(TypeS type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }

  @Override
  public TypeS evaluationType() {
    return type();
  }

  @Override
  public NamedList<Item> evaluationParameters() {
    return NamedList.empty();
  }
}


