package org.smoothbuild.lang.base.type;

/**
 * This class is immutable.
 */
public abstract class BaseType extends Type {
  public BaseType(String name) {
    super(name, new TypeConstructor(name), false);
  }
}

