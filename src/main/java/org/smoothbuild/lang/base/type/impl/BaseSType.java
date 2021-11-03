package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.lang.base.type.api.BaseType;

/**
 * This class is immutable.
 */
public class BaseSType extends SType implements BaseType {
  public BaseSType(String name) {
    super(name, set());
  }
}

