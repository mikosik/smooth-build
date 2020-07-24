package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;

import org.smoothbuild.lang.base.type.property.BasicProperties;

public abstract class ConcreteBasicType extends ConcreteType {
  public ConcreteBasicType(String name) {
    super(name, internal(), null, new BasicProperties());
  }
}
