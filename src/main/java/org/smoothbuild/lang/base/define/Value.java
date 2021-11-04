package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public abstract class Value extends GlobalReferencable {
  public Value(TypeS type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }

  @Override
  public TypeS evaluationType() {
    return type();
  }

  @Override
  public ImmutableList<Item> evaluationParameters() {
    return list();
  }
}


